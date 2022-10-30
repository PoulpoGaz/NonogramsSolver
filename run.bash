PATH_TO_PROJECT=$('pwd')

CLASSPATH="$PATH_TO_PROJECT"/target/classes:\
"$HOME"/.m2/repository/info/picocli/picocli/4.6.3/picocli-4.6.3.jar:\
"$HOME"/.m2/repository/org/dom4j/dom4j/2.1.3/dom4j-2.1.3.jar:\
"$HOME"/.m2/repository/jaxen/jaxen/1.2.0/jaxen-1.2.0.jar

JVM_ARGS="-Dfile.encoding=UTF-8 -classpath ${CLASSPATH} fr.poulpogaz.nonogramssolver.cli.Main"

# java $JVM_ARGS --image example/liberty.png -s 32 -o example/liberty.png

for f in example/*.png; do
  echo "$f"
  java $JVM_ARGS --image "$f" -s 16 -o "temp.gif"
  ffmpeg -y -i "temp.gif" -loop -1 "${f//.png/.gif}" > /dev/null 2>&1
done

rm "temp.gif"
