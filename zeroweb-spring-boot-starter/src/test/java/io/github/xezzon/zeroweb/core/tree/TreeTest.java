package io.github.xezzon.zeroweb.core.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class TreeTest {

  public static final List<Menu> DATA_SET = new ArrayList<>();
  private final MenuService menuService = new MenuService();

  @BeforeEach
  void setUp() {
    DATA_SET.clear();
    DATA_SET.add(new Menu("1", "0"));
    DATA_SET.add(new Menu("2", "0"));
    DATA_SET.add(new Menu("3", "0"));
    DATA_SET.add(new Menu("11", "1"));
    DATA_SET.add(new Menu("12", "1"));
    DATA_SET.add(new Menu("13", "1"));
    DATA_SET.add(new Menu("21", "2"));
    DATA_SET.add(new Menu("22", "2"));
    DATA_SET.add(new Menu("121", "12"));
    DATA_SET.add(new Menu("122", "12"));
    DATA_SET.add(new Menu("131", "13"));
    DATA_SET.add(new Menu("1211", "121"));
    DATA_SET.add(new Menu("1221", "122"));
    DATA_SET.add(new Menu("1222", "122"));
    DATA_SET.add(new Menu("1311", "131"));
  }

  @Test
  void topDownList() {
    List<Menu> menus1 = menuService.topDownList(Collections.singleton("0"), -1);
    Assertions.assertEquals(15, menus1.size());
    List<Menu> menus2 = menuService.topDownList(Collections.singleton("1"), -1);
    Assertions.assertEquals(10, menus2.size());
    List<Menu> menus3 = menuService.topDownList(Collections.singleton("0"), 2);
    Assertions.assertEquals(8, menus3.size());
  }

  @Test
  void topDownTree() {
    List<Menu> menus = menuService.topDownTree(Collections.singleton("0"), -1);
    Assertions.assertEquals("1", menus.get(0).getId());
    Assertions.assertEquals("2", menus.get(1).getId());
    Assertions.assertEquals("3", menus.get(2).getId());
    Assertions.assertEquals("11", menus.get(0).getChildren().get(0).getId());
    Assertions.assertEquals(
        "121",
        menus.get(0).getChildren().get(1).getChildren().getFirst().getId()
    );
    Assertions.assertEquals(
        "1222",
        menus.getFirst().getChildren()
            .get(1).getChildren()
            .get(1).getChildren()
            .get(1).getId()
    );
  }

  @Test
  void topDownTreeFiniteDepth() {
    // Test with depth 0: nodes should not have any children.
    List<Menu> menusDepth0 = menuService.topDownTree(Collections.singleton("0"), 0);
    menusDepth0.forEach(menu -> Assertions.assertTrue(
        menu.getChildren() == null || menu.getChildren().isEmpty(),
        "Expected no children at depth 0"
    ));

    // Test with depth 1: immediate children are available, but grandchildren should be pruned.
    List<Menu> menusDepth1 = menuService.topDownTree(Collections.singleton("0"), 1);
    menusDepth1.forEach(menu -> {
      if (menu.getChildren() != null) {
        menu.getChildren().forEach(child -> Assertions.assertTrue(
            child.getChildren() == null || child.getChildren().isEmpty(),
            "Expected no grandchildren at depth 1"
        ));
      }
    });

    // Test with depth 2: children and grandchildren are available, but great-grandchildren are pruned.
    List<Menu> menusDepth2 = menuService.topDownTree(Collections.singleton("0"), 2);
    menusDepth2.forEach(menu -> {
      if (menu.getChildren() != null) {
        menu.getChildren().forEach(child -> {
          if (child.getChildren() != null) {
            child.getChildren().forEach(grandchild -> Assertions.assertTrue(
                grandchild.getChildren() == null || grandchild.getChildren().isEmpty(),
                "Expected no great-grandchildren at depth 2"
            ));
          }
        });
      }
    });
  }

}

class Menu implements ITreeNode<Menu, String> {

  String id;
  String parentId;
  List<Menu> children;

  Menu(String id, String parentId) {
    super();
    this.id = id;
    this.parentId = parentId;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getParentId() {
    return this.parentId;
  }

  @Override
  public List<Menu> getChildren() {
    return this.children;
  }

  @Override
  public void setChildren(List<Menu> children) {
    this.children = children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Menu menu = (Menu) o;
    return Objects.equals(id, menu.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

class MenuService implements ITreeService<Menu, String> {

  @Override
  public List<Menu> listByParentId(Collection<String> parentIds) {
    return TreeTest.DATA_SET.stream()
        .filter(menu -> parentIds.contains(menu.getParentId()))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
