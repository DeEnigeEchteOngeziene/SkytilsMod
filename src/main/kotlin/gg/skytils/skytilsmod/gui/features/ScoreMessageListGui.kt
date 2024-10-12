/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2024 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.gui.features

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.vigilance.utils.onLeftClick
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.core.PersistentSave
import gg.skytils.skytilsmod.features.impl.handlers.DungeonScoreMessageList
import gg.skytils.skytilsmod.gui.ReopenableGUI
import gg.skytils.skytilsmod.gui.components.SimpleButton
import java.awt.Color

class ScoreMessageListGui(private val dungeonScore: Int): WindowScreen(ElementaVersion.V2, newGuiScale = 2), ReopenableGUI {

    private val scrollComponent: ScrollComponent

    init {
        UIText("Message List For $dungeonScore Score").childOf(window).constrain {
            x = CenterConstraint()
            y = RelativeConstraint(0.075f)
            height = 14.pixels()
        }

        scrollComponent = ScrollComponent(
            innerPadding = 4f,
        ).childOf(window).constrain {
            x = RelativeConstraint(0.1f)
            y = RelativeConstraint(0.15f)
            width = 80.percent()
            height = 70.percent() + 2.pixels()
        }

        val bottomButtons = UIContainer().childOf(window).constrain {
            x = CenterConstraint()
            y = 90.percent()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        SimpleButton("Save and Exit").childOf(bottomButtons).constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onLeftClick {
            mc.displayGuiScreen(null)
        }

        SimpleButton("Add Message").childOf(bottomButtons).constrain {
            x = SiblingConstraint(5f)
            y = 0.pixels()
        }.onLeftClick {
            addNewMessage()
        }

        for (message in DungeonScoreMessageList.messages) {
            if (message.value == dungeonScore) {
                addNewMessage(message.key)
            }
        }
    }

    private fun addNewMessage(message: String = "") {
        val container = UIContainer().childOf(scrollComponent).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            width = 80.percent()
            height = 9.5.percent()
        }.effect(OutlineEffect(Color(0, 243, 255), 1f))

        val textBox = UITextInput("Add Message Here").childOf(container).constrain {
            x = 5.pixels()
            y = CenterConstraint()
            width = 85.percent()
        }.also {
            it.setText(message)
            it.onKeyType { _, _ ->
                it.setText(it.getText())
            }
        }

        SimpleButton("Remove").childOf(container).constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            height = 75.percent()
        }.onLeftClick {
            scrollComponent.removeChild(container)
        }

        container.onLeftClick {
            textBox.grabWindowFocus()
        }
    }

    override fun onScreenClose() {
        super.onScreenClose()
        DungeonScoreMessageList.messages.clear()

        for (container in scrollComponent.allChildren) {
            val text = container.childrenOfType<UITextInput>().firstOrNull()
                ?: throw IllegalStateException("${container.componentName} does not have a UITextInput which cannot be missing! Available children ${container.children.map { it.componentName }}")
            val name = text.getText()
            DungeonScoreMessageList.messages[name] = dungeonScore
        }

        if (dungeonScore == 270) {
            for (message in DungeonScoreMessageList.score300Messages) {
                DungeonScoreMessageList.messages[message] = 300
            }

            DungeonScoreMessageList.updateScore270Messages()
        }

        if (dungeonScore == 300) {
            for (message in DungeonScoreMessageList.score270Messages) {
                DungeonScoreMessageList.messages[message] = 270
            }

            DungeonScoreMessageList.updateScore300Messages()
        }

        PersistentSave.markDirty<DungeonScoreMessageList>()
    }
}