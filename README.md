# Geom

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=xezzon_geom-spring-boot)](https://sonarcloud.io/summary/new_code?id=xezzon_geom-spring-boot)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xezzon_geom-spring-boot&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xezzon_geom-spring-boot)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/049cec67356449e2a95502b441ad005f)](https://app.codacy.com/gh/xezzon/geom-spring-boot/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

基于 Spring Boot 框架的一组微服务构件，包含认证、后台管理、开放平台等服务。提供 SDK 与服务进行交互。

不同于市面上主流的后台管理系统，本项目的定位不是脚手架。本项目希望使用者通过独立部署服务的方式，作为一个系统的一部分提供功能。对于使用者而言，本项目的内部实现应视为黑箱。使用者无需（也不应该）对本项目的源代码进行二次开发，而是通过SDK提供的API与服务进行交互。

## 功能特性

- [后台管理服务](./geom-service/geom-service-admin/README.md)
  - 认证
- [服务间接口SDK](./geom-proto/README.md)

## [开发者手册](./CONTRIBUTING.md)

## [技术栈](https://xezzon.github.io/geom-spring-boot/dependencies.html)

## [License](https://xezzon.github.io/geom-spring-boot/licenses.html)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fxezzon%2Fgeom-spring-boot.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fxezzon%2Fgeom-spring-boot?ref=badge_large)
