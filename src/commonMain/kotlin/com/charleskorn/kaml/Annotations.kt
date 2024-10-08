/*

   Copyright 2018-2023 Charles Korn.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.charleskorn.kaml

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

/**
 * Adds a comment block before property on serialization
 * @property lines comment lines to add
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
public annotation class YamlComment(
    vararg val lines: String,
)

/**
 * Write a String value if it is a single line in the specified ScalarStyle.
 * This overrides the value specified in the [YamlConfiguration].
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
public annotation class YamlSingleLineStringStyle(
    val singleLineStringStyle: SingleLineStringStyle,
)

/**
 * Write a String value if it is a multiline in the specified ScalarStyle.
 * This overrides the value specified in the [YamlConfiguration].
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
public annotation class YamlMultiLineStringStyle(
    val multiLineStringStyle: MultiLineStringStyle,
)
