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

import java.util.Collections;
import java.util.Map;
import lombok.Getter;

/**
 * ZeroWeb 业务异常
 * @author xezzon
 */
public abstract class ZerowebBusinessException extends RuntimeException {

  @Getter
  private final transient Map<String, Object> parameters;

  protected ZerowebBusinessException(String message) {
    super(message);
    this.parameters = Collections.emptyMap();
  }

  protected ZerowebBusinessException(
      final Map<String, Object> parameters,
      final String message
  ) {
    super(message);
    this.parameters = parameters;
  }

  public abstract String getCode();

  public int getHttpStatus() {
    return ErrorCodeConstant.CLIENT_ERROR_STATUS;
  }
}
