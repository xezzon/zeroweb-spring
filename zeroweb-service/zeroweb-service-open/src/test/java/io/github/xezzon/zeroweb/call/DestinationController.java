package io.github.xezzon.zeroweb.call;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/httpbin")
public class DestinationController {

  @RequestMapping("/anything/{anything}")
  @SaCheckLogin
  public String anything(@PathVariable String anything, @RequestParam String hello) {
    return anything + "," + hello;
  }
}
