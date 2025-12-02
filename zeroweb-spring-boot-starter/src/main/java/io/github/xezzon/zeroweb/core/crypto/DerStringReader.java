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

import java.util.Base64;

/**
 * @author xezzon
 */
public class DerStringReader implements ASN1PublicKeyReader {

  private final Base64.Decoder decoder = Base64.getDecoder();
  private final String derBase64;

  public DerStringReader(String derBase64) {
    this.derBase64 = derBase64;
  }

  @Override
  public Object readPublicKey() {
    return decoder.decode(derBase64);
  }
}
