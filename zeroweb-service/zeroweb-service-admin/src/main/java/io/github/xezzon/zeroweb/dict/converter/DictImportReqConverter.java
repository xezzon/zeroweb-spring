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

package io.github.xezzon.zeroweb.dict.converter;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.DictImportReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// @author xezzon
@Mapper
public interface DictImportReqConverter extends From<DictImportReq, Dict> {

  DictImportReqConverter INSTANCE = Mappers.getMapper(DictImportReqConverter.class);


  @Mapping(target = "parentId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", constant = "true")
  @Mapping(target = "editable", constant = "false")
  @Mapping(target = "children", ignore = true)
  @Mapping(target = "tag", source = "tag", defaultValue = Dict.DICT_TAG)
  @Override
  Dict from(DictImportReq source);
}
