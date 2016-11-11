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

import java.io.IOException;
import java.util.List;

/**
 * Find input files.
 */
@FunctionalInterface
public interface InputFileFinder {
  /**
   * Collect the input files and return as a List.
   *
   * @return List of input files
   * @throws IOException in case of I/O error
   */
  List<ClientInputFile> collect() throws IOException;
}
