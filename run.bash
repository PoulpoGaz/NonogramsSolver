PATH_TO_PROJECT=$('pwd')

CLASSPATH="$PATH_TO_PROJECT"/target/classes:\
"$HOME"/.m2/repository/info/picocli/picocli/4.6.3/picocli-4.6.3.jar:\
"$HOME"/.m2/repository/org/dom4j/dom4j/2.1.3/dom4j-2.1.3.jar:\
"$HOME"/.m2/repository/jaxen/jaxen/1.2.0/jaxen-1.2.0.jar:\
"$HOME"/.m2/repository/org/apache/logging/log4j/log4j-api/2.18.0/log4j-api-2.18.0.jar:\
"$HOME"/.m2/repository/org/apache/logging/log4j/log4j-core/2.18.0/log4j-core-2.18.0.jar

JVM_ARGS="-Dfile.encoding=UTF-8 -classpath ${CLASSPATH} fr.poulpogaz.nonogramssolver.Main"

for f in example/**.png; do
  echo "$f"
  # if ! [ -f "${f//.png/.gif}" ]; then
    java $JVM_ARGS --image "$f" -s 16 -o "temp.gif"
    # wow
    ffmpeg -y -i "temp.gif" -filter_complex "[0:v] palettegen [p]; [0:v][p] paletteuse" -loop -1 "${f//.png/.gif}" > /dev/null 2>&1
  # fi
done

rm "temp.gif"
