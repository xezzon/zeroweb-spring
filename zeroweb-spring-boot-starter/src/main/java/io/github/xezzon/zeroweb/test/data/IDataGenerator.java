package io.github.xezzon.zeroweb.test.data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.TestOnly;

/**
 * @author xezzon
 */
@TestOnly
public interface IDataGenerator<T> {

  /// 生成一个数据的方法
  T generateData(int i);

  /**
   * 生成数据
   */
  default List<T> generate(int count) {
    return IntStream.range(0, count)
        .mapToObj(this::generateData)
        .collect(Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
              Collections.shuffle(list);
              return list;
            }
        ));
  }
}
