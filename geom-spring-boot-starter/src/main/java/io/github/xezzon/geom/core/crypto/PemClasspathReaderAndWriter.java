package io.github.xezzon.geom.core.crypto;

import io.github.xezzon.geom.core.util.ResourceUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * 从classpath中读写PEM格式的公钥和私钥文件
 * @author xezzon
 */
public class PemClasspathReaderAndWriter implements
    ASN1PublicKeyReader, ASN1PrivateKeyReader,
    ASN1PublicKeyWriter, ASN1PrivateKeyWriter {

  /**
   * PEM文件默认后缀名
   */
  public static final String FILE_SUFFIX = ".pem";
  /**
   * 公钥PEM文件后缀名
   */
  public static final String PUBLIC_KEY_FILE_SUFFIX = ".public" + FILE_SUFFIX;
  /**
   * 私钥PEM文件后缀名
   */
  public static final String PRIVATE_KEY_FILE_SUFFIX = ".secret" + FILE_SUFFIX;
  /**
   * PEM文件公钥默认头尾描述符
   */
  public static final String PEM_PUBLIC_KEY_HEADER = "PUBLIC KEY";
  /**
   * PEM文件私钥默认头尾描述符
   */
  public static final String PEM_PRIVATE_KEY_HEADER = "PRIVATE KEY";

  /**
   * PEM名称
   */
  private final String name;

  public PemClasspathReaderAndWriter(String name) {
    this.name = name;
  }

  /**
   * 从classpath的PEM文件中读取公钥
   * 文件名格式为：`${name}.public.pem`
   * @return ASN.1形式的公钥
   */
  public Object readPublicKey() throws IOException {
    String filename = name + PUBLIC_KEY_FILE_SUFFIX;
    return this.readKey(filename);
  }

  /**
   * 从classpath的PEM文件中读取私钥
   * 文件名格式为：`${name}.secret.pem`
   * @return ASN.1形式的私钥
   */
  @Override
  public Object readPrivateKey() throws IOException {
    String filename = name + PRIVATE_KEY_FILE_SUFFIX;
    return this.readKey(filename);
  }

  /**
   * 向classpath的PEM文件中写入公钥
   * 文件名格式为：`${name}.public.pem`
   * @param publicKey 公钥
   */
  @Override
  public void writePublicKey(byte[] publicKey) throws IOException {
    String filename = name + PUBLIC_KEY_FILE_SUFFIX;
    File keyFile = ResourceUtil.getResourceFromClasspath(filename).toFile();
    try (PemWriter pemWriter = new PemWriter(new FileWriter(keyFile))) {
      PemObject pemObject = new PemObject(PEM_PUBLIC_KEY_HEADER, publicKey);
      pemWriter.writeObject(pemObject);
    }
  }

  /**
   * 向classpath的PEM文件中写入私钥
   * 文件名格式为：`${name}.secret.pem`
   * @param privateKey 私钥
   */
  @Override
  public void writePrivateKey(byte[] privateKey) throws IOException {
    String filename = name + PRIVATE_KEY_FILE_SUFFIX;
    File keyFile = ResourceUtil.getResourceFromClasspath(filename).toFile();
    try (PemWriter pemWriter = new PemWriter(new FileWriter(keyFile))) {
      PemObject pemObject = new PemObject(PEM_PRIVATE_KEY_HEADER, privateKey);
      pemWriter.writeObject(pemObject);
    }
  }

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
