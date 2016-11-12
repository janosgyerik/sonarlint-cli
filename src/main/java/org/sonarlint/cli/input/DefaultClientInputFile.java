package org.sonarlint.cli.input;

import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

class DefaultClientInputFile implements ClientInputFile {
  private final Path path;
  private final boolean test;
  private final Charset charset;

  DefaultClientInputFile(Path path, boolean test, Charset charset) {
    this.path = path;
    this.test = test;
    this.charset = charset;
  }

  @Override
  public boolean isTest() {
    return test;
  }

  @Override
  public String getPath() {
    return path.toString();
  }

  @Override
  public Charset getCharset() {
    return charset;
  }

  @Override
  public <G> G getClientObject() {
    return null;
  }

  @Override
  public InputStream inputStream() throws IOException {
    return Files.newInputStream(path);
  }

  @Override
  public String contents() throws IOException {
    return new String(Files.readAllBytes(path));
  }
}
