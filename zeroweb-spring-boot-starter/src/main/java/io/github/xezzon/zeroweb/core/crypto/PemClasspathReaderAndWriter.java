/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.core.crypto;

import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

/// PemClasspathReaderAndWriter 提供了从classpath中读写PEM格式的公钥和私钥文件的功能。
/// 它实现了 [ASN1PublicKeyReader], [ASN1PrivateKeyReader], [ASN1PublicKeyWriter], [ASN1PrivateKeyWriter] 接口，从而支持对密钥的读取和写入操作。
/// @author xezzon
@SuppressWarnings("ClassCanBeRecord")
public class PemClasspathReaderAndWriter implements
    ASN1PublicKeyReader, ASN1PrivateKeyReader,
    ASN1PublicKeyWriter, ASN1PrivateKeyWriter {

  /// PEM文件的默认后缀名，值为 ".pem"。
  public static final String FILE_SUFFIX = ".pem";
  /// 公钥PEM文件后缀名，值为 ".public.pem"。
  public static final String PUBLIC_KEY_FILE_SUFFIX = ".public" + FILE_SUFFIX;
  /// 私钥PEM文件后缀名，值为 ".secret.pem"。
  public static final String PRIVATE_KEY_FILE_SUFFIX = ".secret" + FILE_SUFFIX;
  /// PEM文件中公钥块的默认头尾描述符，值为 "PUBLIC KEY"。
  public static final String PEM_PUBLIC_KEY_HEADER = "PUBLIC KEY";
  /// PEM文件中私钥块的默认头尾描述符，值为 "PRIVATE KEY"。
  public static final String PEM_PRIVATE_KEY_HEADER = "PRIVATE KEY";

  /// 当前PEM文件的逻辑名称，用于构成实际的文件名。
  private final String name;

  /// 构造一个新的 PemClasspathReaderAndWriter 实例。
  ///
  /// @param name PEM文件的逻辑名称，例如 "my-app-key"。
  public PemClasspathReaderAndWriter(String name) {
    this.name = name;
  }

  /// 从classpath的PEM文件中读取公钥。
  /// 文件名格式为：`${name}.public.pem`。
  ///
  /// @return ASN.1形式的公钥对象。
  /// @throws IOException 如果读取文件时发生I/O错误。
  public Object readPublicKey() throws IOException {
    String filename = name + PUBLIC_KEY_FILE_SUFFIX;
    return this.readKey(filename);
  }

  /// 从classpath的PEM文件中读取私钥。
  /// 文件名格式为：`${name}.secret.pem`。
  ///
  /// @return ASN.1形式的私钥对象。
  /// @throws IOException 如果读取文件时发生I/O错误。
  @Override
  public Object readPrivateKey() throws IOException {
    String filename = name + PRIVATE_KEY_FILE_SUFFIX;
    return this.readKey(filename);
  }

  /// 向classpath的PEM文件中写入公钥。
  /// 文件名格式为：`${name}.public.pem`。
  ///
  /// @param publicKey 待写入的公钥字节数组。
  /// @throws IOException 如果写入文件时发生I/O错误。
  @Override
  public void writePublicKey(byte[] publicKey) throws IOException {
    String filename = name + PUBLIC_KEY_FILE_SUFFIX;
    File keyFile = ResourceUtil.getResourceFromClasspath(filename).toFile();
    Files.createDirectories(keyFile.toPath().getParent());
    try (PemWriter pemWriter = new PemWriter(new FileWriter(keyFile))) {
      PemObject pemObject = new PemObject(PEM_PUBLIC_KEY_HEADER, publicKey);
      pemWriter.writeObject(pemObject);
    }
  }

  /// 向classpath的PEM文件中写入私钥。
  /// 文件名格式为：`${name}.secret.pem`。
  ///
  /// @param privateKey 待写入的私钥字节数组。
  /// @throws IOException 如果写入文件时发生I/O错误。
  @Override
  public void writePrivateKey(byte[] privateKey) throws IOException {
    String filename = name + PRIVATE_KEY_FILE_SUFFIX;
    File keyFile = ResourceUtil.getResourceFromClasspath(filename).toFile();
    Files.createDirectories(keyFile.toPath().getParent());
    try (PemWriter pemWriter = new PemWriter(new FileWriter(keyFile))) {
      PemObject pemObject = new PemObject(PEM_PRIVATE_KEY_HEADER, privateKey);
      pemWriter.writeObject(pemObject);
    }
  }

  /// 从指定的PEM文件中读取密钥。
  ///
  /// @param filename 要读取的PEM文件名（相对于classpath）。
  /// @return 读取到的密钥对象。
  /// @throws IOException 如果读取文件时发生I/O错误。
  private Object readKey(String filename) throws IOException {
    Path publicKeyPath = ResourceUtil.getResourceFromClasspath(filename);
    try (
        FileReader reader = new FileReader(publicKeyPath.toFile());
        PEMParser pemParser = new PEMParser(reader)
    ) {
      return pemParser.readObject();
    }
  }
}
