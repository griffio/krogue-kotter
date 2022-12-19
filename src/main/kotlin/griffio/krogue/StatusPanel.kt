package griffio.krogue

import com.varabyte.kotter.foundation.render.OffscreenBuffer
import com.varabyte.kotter.foundation.render.OffscreenRenderScope
import com.varabyte.kotter.foundation.render.offscreen
import com.varabyte.kotter.foundation.text.green
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.render.RenderScope
import com.varabyte.kotterx.decorations.BorderCharacters

data class StatusPanel(
    val paddingLeftRight: Int = 0,
    val paddingTopBottom: Int = 0,
    val offScreenBuffer: OffscreenBuffer,
    val endOfLine: Boolean = false,
) {
    val borderCharacters: BorderCharacters = BorderCharacters.CURVED
    val maxWidth = offScreenBuffer.lineLengths.maxOrNull() ?: 0
    val maxWidthWithPadding = maxWidth + paddingLeftRight * 2
}

fun RenderScope.statusPanelBorderPadding(statusPanel: StatusPanel, leftBorder: Char, rightBorder: Char) {
    text(leftBorder)
    statusPanel.borderCharacters.horiz.toString().repeat(statusPanel.maxWidthWithPadding).let { text(it) }
    if (statusPanel.endOfLine) textLine(rightBorder) else text(rightBorder)
}

fun RenderScope.statusPanelVerticalPadding(statusPanel: StatusPanel) {
    for (i in 0 until statusPanel.paddingTopBottom) {
        text(statusPanel.borderCharacters.vert)
        text(" ".repeat(statusPanel.maxWidthWithPadding))
        if (statusPanel.endOfLine) textLine(statusPanel.borderCharacters.vert) else text(statusPanel.borderCharacters.vert)
    }
}

fun RenderScope.statusPanelRender(statusPanel: StatusPanel) {
    val renderer = statusPanel.offScreenBuffer.createRenderer()
    for (i in statusPanel.offScreenBuffer.lineLengths.indices) {
        text(statusPanel.borderCharacters.vert)
        text(" ".repeat(statusPanel.paddingLeftRight))
        renderer.renderNextRow()
        repeat(statusPanel.maxWidth - statusPanel.offScreenBuffer.lineLengths[i]) { text(" ") }
        text(" ".repeat(statusPanel.paddingLeftRight))
        if (statusPanel.endOfLine) textLine(statusPanel.borderCharacters.vert) else text(statusPanel.borderCharacters.vert)
    }
}

fun RenderScope.statusPanels(
    paddingLeftRight: Int = 0,
    paddingTopBottom: Int = 0,
    health: OffscreenRenderScope.() -> Unit,
    cash: OffscreenRenderScope.() -> Unit,
) {
    val healthContent = StatusPanel(paddingLeftRight, paddingTopBottom, offscreen(health))
    val cashContent = StatusPanel(paddingLeftRight, paddingTopBottom, offscreen(cash), endOfLine = true)

    statusPanelBorderPadding(
        healthContent,
        healthContent.borderCharacters.topLeft,
        healthContent.borderCharacters.topRight
    )

    statusPanelBorderPadding(
        cashContent,
        cashContent.borderCharacters.topLeft,
        cashContent.borderCharacters.topRight
    )

    statusPanelVerticalPadding(healthContent)

    statusPanelVerticalPadding(cashContent)

    statusPanelRender(healthContent)

    statusPanelRender(cashContent)

    statusPanelVerticalPadding(healthContent)

    statusPanelVerticalPadding(healthContent)

    statusPanelBorderPadding(
        healthContent,
        healthContent.borderCharacters.botLeft,
        healthContent.borderCharacters.botRight
    )

    statusPanelBorderPadding(
        cashContent,
        cashContent.borderCharacters.botLeft,
        cashContent.borderCharacters.botRight
    )
}
