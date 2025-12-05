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

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/// ## 密钥读写工具类
///
/// `SecretKeyUtil` 提供了一系列静态方法，用于处理公钥和私钥的读取与写入操作。
/// 它封装了底层密码学库的复杂性，使得在 ZeroWeb 框架中进行密钥管理变得更加简洁和安全。
///
/// **主要功能:**
/// - 从 ASN.1 格式的输入源读取公钥和私钥。
/// - 将公钥和私钥写入 ASN.1 格式的输出目标。
///
/// 本工具类依赖于 `Bouncy Castle` 库进行密钥格式转换和处理。
///
/// @author xezzon
@Slf4j
public class SecretKeyUtil {

  /// 用于 PEM 格式密钥与 JCA 密钥对象之间转换的工具。
  /// 此转换器由 Bouncy Castle 库提供，用于简化密钥的导入和导出操作。
  public static final JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

  /// 私有构造函数，防止外部实例化。
  /// 这是一个工具类，其所有方法均为静态方法，无需创建实例。
  private SecretKeyUtil() {
  }

  /// 从提供的 `ASN1PublicKeyReader` 中读取公钥。
  ///
  /// 此方法通过读取 ASN.1 编码的公钥信息，并使用 `JcaPEMKeyConverter`
  /// 将其转换为 `java.security.PublicKey` 对象。
  ///
  /// @param reader 用于读取 ASN.1 格式公钥的读取器。
  /// @return 解析并转换后的 `PublicKey` 对象。
  /// @throws PEMException 如果在读取或转换过程中发生密码学相关的错误，则抛出此异常。
  public static PublicKey readPublicKey(ASN1PublicKeyReader reader) throws PEMException {
    Object asn1;
    try {
      asn1 = reader.readPublicKey();
    } catch (Exception e) {
      throw new PEMException("Failed to read public key.", e);
    }
    SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(asn1);
    return keyConverter.getPublicKey(publicKeyInfo);
  }

  /// 从提供的 `ASN1PrivateKeyReader` 中读取私钥。
  ///
  /// 此方法通过读取 ASN.1 编码的私钥信息，并使用 `JcaPEMKeyConverter`
  /// 将其转换为 `java.security.PrivateKey` 对象。
  ///
  /// @param reader 用于读取 ASN.1 格式私钥的读取器。
  /// @return 解析并转换后的 `PrivateKey` 对象。
  /// @throws PEMException 如果在读取或转换过程中发生密码学相关的错误，则抛出此异常。
  public static PrivateKey readPrivateKey(ASN1PrivateKeyReader reader) throws PEMException {
    Object asn1;
    try {
      asn1 = reader.readPrivateKey();
    } catch (Exception e) {
      throw new PEMException("Failed to read private key.", e);
    }
    PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(asn1);
    return keyConverter.getPrivateKey(privateKeyInfo);
  }

  /// 将公钥写入到提供的 `ASN1PublicKeyWriter` 中。
  ///
  /// 此方法将 `PublicKey` 对象的编码形式写入到 ASN.1 格式的输出流中。
  ///
  /// @param publicKey 要写入的 `PublicKey` 对象。
  /// @param writer 用于写入 ASN.1 格式公钥的写入器。
  /// @throws PEMException 如果在写入过程中发生 I/O 错误或密码学相关的错误，则抛出此异常。
  public static void writePublicKey(PublicKey publicKey, ASN1PublicKeyWriter writer)
      throws PEMException {
    try {
      writer.writePublicKey(publicKey.getEncoded());
    } catch (IOException e) {
      throw new PEMException("Failed to write public key.", e);
    }
  }

  /// 将私钥写入到提供的 `ASN1PrivateKeyWriter` 中。
  ///
  /// 此方法将 `PrivateKey` 对象的编码形式写入到 ASN.1 格式的输出流中。
  ///
  /// @param privateKey 要写入的 `PrivateKey` 对象。
  /// @param writer 用于写入 ASN.1 格式私钥的写入器。
  /// @throws PEMException 如果在写入过程中发生 I/O 错误或密码学相关的错误，则抛出此异常。
  public static void writePrivateKey(PrivateKey privateKey, ASN1PrivateKeyWriter writer)
      throws PEMException {
    try {
      writer.writePrivateKey(privateKey.getEncoded());
    } catch (IOException e) {
      throw new PEMException("Failed to write public key.", e);
    }
  }
}
