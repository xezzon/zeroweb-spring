package io.github.xezzon.zeroweb.auth.domain;

/**
 * @param username 用户名
 * @param password 口令
 * @author xezzon
 */
public record BasicAuth(String username, String password) {
}
