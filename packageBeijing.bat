set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean package -Pbeijing -Dmaven.test.skip=true && pause