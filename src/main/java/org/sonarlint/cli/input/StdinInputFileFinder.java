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

import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class StdinInputFileFinder implements InputFileFinder {
  private static final String SRC_PREFIX = "src ";
  private static final String TEST_PREFIX = "test ";

  private final Path basedir;
  private final InputStream inputStream;

  StdinInputFileFinder(Path basedir, InputStream inputStream) {
    this.basedir = basedir;
    this.inputStream = inputStream;
  }

  @Override
  public List<ClientInputFile> collect() throws IOException {
    try (
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader reader = new BufferedReader(inputStreamReader)) {
      String line;
      List<ClientInputFile> files = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        String relpath;
        boolean test = false;

        if (line.startsWith(SRC_PREFIX)) {
          relpath = line.substring(SRC_PREFIX.length());
        } else if (line.startsWith(TEST_PREFIX)) {
          relpath = line.substring(TEST_PREFIX.length());
          test = true;
        } else {
          relpath = line;
        }
        Path path = basedir.resolve(relpath);
        files.add(new DefaultClientInputFile(path, test, Charset.defaultCharset()));
      }
      return files;
    }
  }
}
