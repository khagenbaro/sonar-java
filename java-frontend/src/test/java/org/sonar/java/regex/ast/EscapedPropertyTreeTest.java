/*
 * SonarQube Java
 * Copyright (C) 2012-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.regex.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.java.regex.parsertests.RegexParserTestUtils.assertFailParsing;
import static org.sonar.java.regex.parsertests.RegexParserTestUtils.assertSuccessfulParse;

class EscapedPropertyTreeTest {

  /**
   * Following the form \p{Prop} or \P{Prop} (negation)
   */
  @Test
  void escapedProperties() {
    // control
    assertThat("a".matches("\\p{Lower}")).isTrue();
    assertThat("A".matches("\\p{Lower}")).isFalse();

    // POSIX character classes
    assertEscapedProperty("\\\\p{Lower}", "Lower", false);
    assertEscapedProperty("\\\\p{Upper}", "Upper", false);
    assertEscapedProperty("\\\\p{ASCII}", "ASCII", false);
    assertEscapedProperty("\\\\p{Alpha}", "Alpha", false);
    assertEscapedProperty("\\\\p{Digit}", "Digit", false);
    assertEscapedProperty("\\\\p{Alnum}", "Alnum", false);
    assertEscapedProperty("\\\\p{Punct}", "Punct", false);
    assertEscapedProperty("\\\\p{Graph}", "Graph", false);
    assertEscapedProperty("\\\\p{Print}", "Print", false);
    assertEscapedProperty("\\\\p{Blank}", "Blank", false);
    assertEscapedProperty("\\\\p{Cntrl}", "Cntrl", false);
    assertEscapedProperty("\\\\p{XDigit}", "XDigit", false);
    assertEscapedProperty("\\\\p{Space}", "Space", false);

    // java.lang.Character classes
    assertEscapedProperty("\\\\p{javaLowerCase}", "javaLowerCase", false);
    assertEscapedProperty("\\\\p{javaUpperCase}", "javaUpperCase", false);
    assertEscapedProperty("\\\\p{javaWhitespace}", "javaWhitespace", false);
    assertEscapedProperty("\\\\p{javaMirrored}", "javaMirrored", false);

    // Classes for Unicode scripts, blocks, categories and binary properties
    assertEscapedProperty("\\\\p{IsLatin}", "IsLatin", false);
    assertEscapedProperty("\\\\p{InGreek}", "InGreek", false);
    assertEscapedProperty("\\\\p{Lu}", "Lu", false);
    assertEscapedProperty("\\\\p{IsAlphabetic}", "IsAlphabetic", false);
    assertEscapedProperty("\\\\p{Sc}", "Sc", false);

    // Negation
    assertEscapedProperty("\\\\P{InGreek}", "InGreek", true); // Any character except one in the Greek block

    // accept any property, even if it does not exists in hardcoded properties
    assertEscapedProperty("\\\\P{Cowabunga}", "Cowabunga", true); // Any character except one in the Greek block
  }

  @Test
  void failingInvalidEscapedProperties() {
    assertFailParsing("\\\\p", "Expected '{', but found the end of the regex");
    assertFailParsing("\\\\p{", "Expected a property name, but found the end of the regex");
    assertFailParsing("\\\\p{foo", "Expected '}', but found the end of the regex");
    assertFailParsing("\\\\p{}", "Expected a property name, but found '}'");
  }

  private static void assertEscapedProperty(String regex, String expectedProperty, boolean isNegation) {
    RegexTree tree = assertSuccessfulParse(regex);
    assertThat(tree).isInstanceOf(EscapedPropertyTree.class);

    EscapedPropertyTree escapedPropertyTree = (EscapedPropertyTree) tree;
    assertThat(escapedPropertyTree.property()).isEqualTo(expectedProperty);
    assertThat(escapedPropertyTree.isNegation()).isEqualTo(isNegation);
  }

}