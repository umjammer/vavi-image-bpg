[![Release](https://jitpack.io/v/umjammer/vavi-image-bpg.svg)](https://jitpack.io/#umjammer/vavi-image-bpg)
[![Java CI](https://github.com/umjammer/vavi-image-bpg/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-image-bpg/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-image-bpg/actions/workflows/codeql.yml/badge.svg)](https://github.com/umjammer/vavi-image-bpg/actions/workflows/codeql.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-image-bpg

BPG (Better Portable Graphics) decoder over libbpg via JNA.

## Install

### maven

 * https://jitpack.io/#umjammer/vavi-image-bpg

### dynamic library

libbpg

```shell
$ git clone https://github.com/mirrorer/libbpg
$ cd libbpg
$ git fetch --depth 1 origin 0e2aadb
$ git checkout 0e2aadb
$ patch -p 0 < .../vavi-image-bpg/src/main/patch/mac.patch
$ make
$ cp libbpg.dylib /usr/local/lib
```

java system property

```
 -Djna.library.path=/usr/local/lib
```

## Usage

```java
    BufferedImage image = ImageIO.read(Paths.get("/foo/bar.bpg").toFile());
```

## References

 * https://bellard.org/bpg/
 * https://github.com/alexandruc/android-bpg

## TODO