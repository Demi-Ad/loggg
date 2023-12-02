<div align="center">
  <h3 align="center">loggg</h3>
</div>

<!-- GETTING STARTED -->
## Getting Started

loggg 설명문

### Prerequisites
- JAVA 17

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Demi-Ad/loggg.git
   ```
2. Build Project

3. Start Jar
- client
`config.yaml`[example](/logclient/src/main/resources/config.yaml)
```shell
java -jar -Dconfig.path=your/config/path.yaml project.jar
```

- server
`application.yml`[here](/logserver/src/main/resources/application.yml)
```shell
java -jar -Dspring.profiles.active=memory or embedded or custom -Dtcp.port=yourport project.jar
```

> spring.profiles.active : **Optional** Default Value = memory

> tcp.port : **Optional** Default Value = 2777