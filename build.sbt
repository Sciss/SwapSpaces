lazy val baseName       = "SwapSpaces"
lazy val baseNameL      = baseName.toLowerCase
lazy val projectVersion = "0.1.0-SNAPSHOT"

lazy val gitHost        = "codeberg.org"
lazy val gitUser        = "sciss"
lazy val gitRepo        = baseName

lazy val root = project.in(file("."))
  .settings(
    name         := baseName,
    description  := "Materials for a catalogue",
    version      := projectVersion,
    homepage     := Some(url(s"https://$gitHost/$gitUser/$gitRepo")),
    licenses     := Seq("AGPL v3+" -> url("http://www.gnu.org/licenses/agpl-3.0.txt")),
    scalaVersion := "3.2.2",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8"),
    resolvers    += "imagej.releases" at "https://maven.scijava.org/content/repositories/releases/",
    libraryDependencies ++= Seq(
      "com.jhlabs"          %  "filters"              % deps.main.jhlabs,       // image composites
      "de.sciss"            %% "fileutil"             % deps.main.fileUtil,     // utility functions
      "de.sciss"            %% "numbers"              % deps.main.numbers,      // numeric utilities
//      "mpicbg"              %  "mpicbg"               % deps.main.mpicbg,       // 2D transforms
//      "net.imagej"          %  "ij"                   % deps.main.imageJ,       // analyzing image data
      "org.rogach"          %% "scallop"              % deps.main.scallop,      // command line option parsing
    ),
  )

lazy val deps = new {
  lazy val main = new {
    val fileUtil    = "1.1.5"
//    val imageJ      = "1.53t" // "1.54d"
    val jhlabs      = "2.0.235"
//    val mpicbg      = "1.5.0"
    val numbers     = "0.2.1"
    val scallop     = "4.1.0"
  }
}

