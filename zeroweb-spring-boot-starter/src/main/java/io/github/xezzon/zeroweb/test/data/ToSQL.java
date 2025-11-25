package io.github.xezzon.zeroweb.test.data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author xezzon
 */
public interface ToSQL<T> {

  /// @return 表名
  String tableName();

  /// @return 列名
  String[] columnNames();

  List<Function<T, Object>> columnValue();

  /// 列数据模板
  ///
  /// 如果全部是字符串则可以使用默认方法
  ///
  /// @return 默认值 `('%s', '%s',...),`
  default String columnTemplate() {
    return IntStream.range(0, columnNames().length)
        .mapToObj(_ -> "'%s'")
        .collect(Collectors.joining(",", "(", "),"));
  }

  default String toSql(List<T> dataset) {
    StringBuilder sqlBuilder = new StringBuilder();
    // 表头
    sqlBuilder.append("INSERT INTO ").append(tableName()).append("(");
    for (String columnName : columnNames()) {
      sqlBuilder.append(columnName).append(',');
    }
    sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    sqlBuilder.append(") VALUES ");
    // 数据
    for (T data : dataset) {
      Object[] values = columnValue().stream()
          .map(fn -> fn.apply(data))
          .toArray();
      sqlBuilder.append(String.format(columnTemplate(), values));
    }

    sqlBuilder.setCharAt(sqlBuilder.length() - 1, ';');
    return sqlBuilder.toString();
  }
}
