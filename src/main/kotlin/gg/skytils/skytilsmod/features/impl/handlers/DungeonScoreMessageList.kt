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

package gg.skytils.skytilsmod.features.impl.handlers

import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.core.PersistentSave
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.Reader
import java.io.Writer
import java.util.HashMap

object DungeonScoreMessageList : PersistentSave(File(Skytils.modDir, "dungeonscoremessagelist.json")) {

    val messages = HashMap<String, Int>()
    var score270Messages = ArrayList<String>()
    var score300Messages = ArrayList<String>()

    fun updateScore270Messages() {
        updateScoreMessages(score270Messages, 270)
    }

    fun updateScore300Messages() {
        updateScoreMessages(score300Messages, 300)
    }

    private fun updateScoreMessages(scoreMessages: ArrayList<String>, score: Int) {
        scoreMessages.clear()

        for (message in messages) {
            if (message.value == score) {
                scoreMessages.add(message.key)
            }
        }
    }

    override fun read(reader: Reader) {
        val data = json.decodeFromString<SaveMessageData>(reader.readText())
        messages.putAll(data.messages.entries.associate { it.key to it.value.score })

        for(message in data.messages) {
            if(message.value.score == 270) {
                score270Messages.add(message.key)
            }
            if(message.value.score == 300) {
                score300Messages.add(message.key)
            }
        }
    }

    override fun write(writer: Writer) {
        writer.write(
            json.encodeToString(
                SaveMessageData(messages.entries.associate { it.key to SaveMessageComponent(it.value) })
            )
        )
    }

    override fun setDefault(writer: Writer) {
        write(writer)
    }
}

@Serializable
private data class SaveMessageData(val messages: Map<String, SaveMessageComponent>)

@Serializable
private data class SaveMessageComponent(val score: Int)