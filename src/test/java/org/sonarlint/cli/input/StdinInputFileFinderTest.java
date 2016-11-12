/*
 * SonarLint CLI
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonarlint.cli.input;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class StdinInputFileFinderTest {
  @Test
  public void test_one_source_one_test() throws IOException {
    String[] lines = {
      "src Hello.java",
      "test HelloTest.java"
    };
    InputFileFinder fileFinder = new StdinInputFileFinder(Paths.get("."), streamOf(lines));
    assertThat(fileFinder.collect()).extracting("path", "test").containsExactly(
      tuple("./Hello.java", false),
      tuple("./HelloTest.java", true)
    );
  }

  @Test
  public void test_source_by_default() throws IOException {
    String[] lines = {
      "Hello.java",
      "test HelloTest.java"
    };
    InputFileFinder fileFinder = new StdinInputFileFinder(Paths.get("."), streamOf(lines));
    assertThat(fileFinder.collect()).extracting("path", "test").containsExactly(
      tuple("./Hello.java", false),
      tuple("./HelloTest.java", true)
    );
  }

  private InputStream streamOf(String... lines) {
    return new ByteArrayInputStream(String.join("\n", Arrays.asList(lines)).getBytes());
  }
}
