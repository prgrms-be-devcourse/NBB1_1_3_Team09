package com.grepp.nbe1_3_team9.controller.user

import com.grepp.nbe1_3_team9.admin.service.oauth2.KakaoApiService
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.controller.user.dto.ChangePasswordReq
import com.grepp.nbe1_3_team9.controller.user.dto.SignInReq
import com.grepp.nbe1_3_team9.controller.user.dto.SignUpReq
import com.grepp.nbe1_3_team9.controller.user.dto.UpdateProfileReq
import com.grepp.nbe1_3_team9.domain.service.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val kakaoApiService: KakaoApiService
) {

    private val log = LoggerFactory.getLogger(UserController::class.java)

    // 현재 사용자 정보 요청 API
    @GetMapping("/me")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<*> {
        return try {
            val currentUser = userService.getCurrentUserInfo()
            ResponseEntity.ok(currentUser)
        } catch (e: UserException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
        }
    }

    // 회원가입
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpReq: SignUpReq): ResponseEntity<String> {
        userService.register(signUpReq)
        return ResponseEntity.ok("회원가입 성공")
    }

    // 로그인
    @PostMapping("/signin")
    fun signIn(@RequestBody signInReq: SignInReq, response: HttpServletResponse): ResponseEntity<Map<String, Any>> {
        val token = userService.signIn(signInReq, response)
        val responseBody = mapOf(
            "message" to "로그인 성공",
            "data" to token
        )
        return ResponseEntity.ok(responseBody)
    }

    // 카카오 소셜 로그인
    @GetMapping("/signin/kakao")
    fun kakaoLogin(@RequestParam code: String, response: HttpServletResponse): ResponseEntity<String> {
        log.info("카카오 인가 코드: {}", code)
        return try {
            val accessToken = kakaoApiService.getAccessToken(code)
            val kakaoUserInfo = kakaoApiService.getUserInfo(accessToken)

            kakaoApiService.processUser(kakaoUserInfo)

            ResponseEntity.ok("카카오 로그인 성공, JWT 토큰이 쿠키에 저장되었습니다.")
        } catch (e: Exception) {
            log.error("카카오 로그인 처리 중 오류 발생", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 실패")
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication == null || !authentication.isAuthenticated) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되지 않았습니다.")
        } else {
            userService.logout(authentication, response)
            SecurityContextHolder.clearContext()
            ResponseEntity.ok("로그아웃 성공")
        }
    }

    // 회원정보 조회
    @GetMapping("/{userId}")
    fun getUserInfo(@PathVariable userId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    // 회원 정보 수정
    @PutMapping("/{userId}")
    fun updateProfile(
        @PathVariable userId: Long,
        @RequestBody updateProfileReq: UpdateProfileReq
    ): ResponseEntity<String> {
        val loggedInUserId = SecurityContextHolder.getContext().authentication.name.toLong()
        userService.updateProfile(loggedInUserId, userId, updateProfileReq)
        return ResponseEntity.ok("회원 정보 수정 성공")
    }

    // 비밀번호 변경
    @PutMapping("/{userId}/password")
    fun changePassword(
        @PathVariable userId: Long,
        @RequestBody changePasswordReq: ChangePasswordReq,
        principal: Principal?
    ): ResponseEntity<String> {
        return if (principal == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되지 않았습니다.")
        } else {
            val loggedInUserId = SecurityContextHolder.getContext().authentication.name.toLong()
            userService.changePassword(loggedInUserId, userId, changePasswordReq)
            ResponseEntity.ok("비밀번호 변경 성공")
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long, principal: Principal): ResponseEntity<String> {
        val loggedInUserId = SecurityContextHolder.getContext().authentication.name.toLong()
        userService.deleteUser(loggedInUserId, userId)
        return ResponseEntity.ok("회원 탈퇴 성공")
    }
}