package io.github.xezzon.zeroweb.locale.event;

import io.github.xezzon.zeroweb.locale.II18nMessage;

/// 国际化内容删除事件
///
/// @param i18nMessage 国际化内容
public record I18nMessageDeletedEvent(
    II18nMessage i18nMessage
) {

}
