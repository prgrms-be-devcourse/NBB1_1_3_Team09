package com.grepp.nbe1_3_team9.schedule.controller

import com.grepp.nbe1_3_team9.schedule.controller.dto.DeletedData
import com.grepp.nbe1_3_team9.schedule.controller.dto.SavedData
import com.grepp.nbe1_3_team9.schedule.controller.dto.SelectedData
import com.grepp.nbe1_3_team9.schedule.controller.dto.UpdatedData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ScheduleController @Autowired constructor(
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/selectCell") // 클라이언트에서 /app/selectCell로 전송한 메시지 처리
    fun handleCellSelection(selectedData: SelectedData) {
        messagingTemplate.convertAndSend("/topic/selectedCells", selectedData)
    }

    @MessageMapping("/deleteCell")
    fun handleCellDeleteCell(selectedData: SelectedData) {
        messagingTemplate.convertAndSend("/topic/deletedCells", selectedData)
    }

    @MessageMapping("/deletedCell")
    fun handleCellDeleteCellId(deletedData: DeletedData) {
        val pinId = deletedData.pinId
        messagingTemplate.convertAndSend("/topic/deletedCellsId", pinId)
    }

    @MessageMapping("/updatedCell")
    fun handleCellUpdateCell(updatedData: UpdatedData) {
        messagingTemplate.convertAndSend("/topic/updatedCells", updatedData)
    }

    @MessageMapping("/savedCell")
    fun handleCellSavedCell(savedData: SavedData) {
        messagingTemplate.convertAndSend("/topic/savedCells", savedData)
    }
}