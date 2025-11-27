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

package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.Setting_;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class SettingDAO extends BaseDAO<Setting, String, SettingRepository> {

  SettingDAO(final SettingRepository repository) {
    super(repository, Setting.class);
  }

  @Override
  public Page<@NonNull Setting> findAll(final @NonNull ODataQueryOption odata) {
    Sort sort = Sort.by(Order.desc(Setting_.UPDATE_TIME));
    return super.findAll(odata, null, sort);
  }

  @Override
  public ICopier<Setting> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<Setting> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
