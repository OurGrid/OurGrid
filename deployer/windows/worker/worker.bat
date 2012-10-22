setlocal
set OGROOT=[[OGROOT]]
start /b javaw -cp . -Dlog4j.configuration="file:[[LOG4J]]" -Djava.ext.dirs=lib -Xms64m -Xmx1024m org.ourgrid.worker.ui.async.Main
endlocal