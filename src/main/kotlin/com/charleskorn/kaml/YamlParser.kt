/*

   Copyright 2018-2020 Charles Korn.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.charleskorn.kaml

import java.io.StringReader
import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.events.Event
import org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException
import org.snakeyaml.engine.v2.parser.ParserImpl
import org.snakeyaml.engine.v2.scanner.StreamReader

internal class YamlParser(yamlSource: String) {
    private val dummyFileName = "DUMMY_FILE_NAME"
    private val loadSettings = LoadSettings.builder().setLabel(dummyFileName).build()
    private val streamReader = StreamReader(StringReader(yamlSource), loadSettings)
    private val events = ParserImpl(streamReader, loadSettings)

    init {
        consumeEventOfType(Event.ID.StreamStart)

        if (peekEvent().eventId == Event.ID.StreamEnd) {
            throw EmptyYamlDocumentException("The YAML document is empty.", Location(1, 1))
        }

        consumeEventOfType(Event.ID.DocumentStart)
    }

    fun ensureEndOfStreamReached() {
        consumeEventOfType(Event.ID.DocumentEnd)
        consumeEventOfType(Event.ID.StreamEnd)
    }

    fun consumeEvent(): Event = checkEvent { events.next() }
    fun peekEvent(): Event = checkEvent { events.peekEvent() }

    fun consumeEventOfType(type: Event.ID) {
        val event = consumeEvent()

        if (event.eventId != type) {
            throw MalformedYamlException("Unexpected ${event.eventId}, expected $type", Location(event.startMark.get().line, event.startMark.get().column))
        }
    }

    private fun checkEvent(retrieve: () -> Event): Event {
        try {
            return retrieve()
        } catch (e: MarkedYamlEngineException) {
            throw translateYamlEngineException(e)
        }
    }

    private fun translateYamlEngineException(e: MarkedYamlEngineException): MalformedYamlException {
        val contextMessage = if (e.context == null) {
            ""
        } else {
            val contextMark = e.contextMark.get()

            e.context + "\n" +
                " at line ${contextMark.line + 1}, column ${contextMark.column + 1}:\n" +
                contextMark.createSnippet(4, Int.MAX_VALUE) + "\n"
        }

        val problemMark = e.problemMark.get()

        val message = contextMessage +
            translateYamlEngineExceptionMessage(e.problem) + "\n" +
            " at line ${problemMark.line + 1}, column ${problemMark.column + 1}:\n" +
            problemMark.createSnippet(4, Int.MAX_VALUE)

        return MalformedYamlException(message, Location(problemMark.line + 1, problemMark.column + 1))
    }

    private fun translateYamlEngineExceptionMessage(message: String): String = when (message) {
        "mapping values are not allowed here",
        "expected <block end>, but found '<block sequence start>'",
        "expected <block end>, but found '<block mapping start>'" ->
            "$message (is the indentation level of this line or a line nearby incorrect?)"
        else -> message
    }
}
