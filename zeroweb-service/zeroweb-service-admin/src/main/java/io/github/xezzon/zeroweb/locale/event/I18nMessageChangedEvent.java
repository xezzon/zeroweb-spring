package io.github.xezzon.zeroweb.locale.event;

/**
 * 国际化内容变更事件
 *
 * @param oldValue 原国际化内容
 * @param newValue 新的国际化内容
 */
public record I18nMessageChangedEvent(
    II18nMessage oldValue,
    II18nMessage newValue
) {

}
