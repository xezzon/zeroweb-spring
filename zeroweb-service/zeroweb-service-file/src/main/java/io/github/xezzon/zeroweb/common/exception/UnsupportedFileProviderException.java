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

package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import org.jspecify.annotations.NullMarked;

/// 启用的存储后端未正确配置
/// @author xezzon
@NullMarked
public class UnsupportedFileProviderException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "SFC01";

  public UnsupportedFileProviderException(FileProviderEnum provider) {
    super(provider + " is not configured correctly.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return ErrorCodeConstant.SERVER_ERROR_STATUS;
  }
}
