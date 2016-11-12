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

import org.sonarlint.cli.util.Logger;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class GlobInputFileFinder implements InputFileFinder {
  private static final String GLOB_PREFIX = "glob:";
  private static final Logger LOGGER = Logger.get();

  private final Path baseDir;
  private final PathMatcher srcMatcher;
  private final PathMatcher testsMatcher;
  private final PathMatcher excludeMatcher;
  private final Charset charset;

  private static PathMatcher acceptAll = p -> true;
  private static PathMatcher refuseAll = p -> false;

  public GlobInputFileFinder(Path baseDir, @Nullable String srcGlobPattern, @Nullable String testsGlobPattern, @Nullable String excludeGlobPattern, Charset charset) {
    this.baseDir = baseDir;
    this.charset = charset;
    FileSystem fs = FileSystems.getDefault();
    try {
      if (srcGlobPattern != null) {
        srcMatcher = fs.getPathMatcher(GLOB_PREFIX + srcGlobPattern);
      } else {
        srcMatcher = acceptAll;
      }
    } catch (Exception e) {
      LOGGER.error("Error creating matcher for sources with pattern: " + srcGlobPattern);
      throw e;
    }

    try {
      if (testsGlobPattern != null) {
        testsMatcher = fs.getPathMatcher(GLOB_PREFIX + testsGlobPattern);
      } else {
        testsMatcher = refuseAll;
      }
    } catch (Exception e) {
      LOGGER.error("Error creating matcher for tests with pattern: " + testsGlobPattern);
      throw e;
    }

    try {
      if (excludeGlobPattern != null) {
        excludeMatcher = fs.getPathMatcher(GLOB_PREFIX + excludeGlobPattern);
      } else {
        excludeMatcher = refuseAll;
      }
    } catch (Exception e) {
      LOGGER.error("Error creating matcher for exclusions with pattern: " + excludeGlobPattern);
      throw e;
    }
  }

  @Override
  public List<ClientInputFile> collect() throws IOException {
    final List<ClientInputFile> files = new ArrayList<>();
    Files.walkFileTree(baseDir, new FileCollector(baseDir, files));
    return files;
  }

  private class FileCollector extends SimpleFileVisitor<Path> {
    private final List<ClientInputFile> files;
    private final Path baseDir;

    private FileCollector(Path baseDir, List<ClientInputFile> files) {
      this.baseDir = baseDir;
      this.files = files;
    }

    @Override
    public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
      Path absoluteFilePath = file;
      Path relativeFilePath = baseDir.relativize(absoluteFilePath);
      boolean isSrc = srcMatcher.matches(absoluteFilePath) || srcMatcher.matches(relativeFilePath);
      boolean isExcluded = excludeMatcher.matches(absoluteFilePath) || excludeMatcher.matches(relativeFilePath);
      if (isSrc && !isExcluded) {
        boolean isTest = testsMatcher.matches(absoluteFilePath) || testsMatcher.matches(relativeFilePath);
        files.add(new DefaultClientInputFile(absoluteFilePath, isTest, charset));
      }

      return super.visitFile(absoluteFilePath, attrs);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
      if (Files.isHidden(dir)) {
        LOGGER.debug("Ignoring hidden directory: " + dir.toString());
        return FileVisitResult.SKIP_SUBTREE;
      }

      return super.preVisitDirectory(dir, attrs);
    }
  }

}