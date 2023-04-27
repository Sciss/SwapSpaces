val apiKey = "???" // deepai key here

def run(idx: Int): Unit = {
  println(s"Processing image $idx...")
  import sys.process._
  val cmdGen = Seq("curl",
    "-F", f"image=https://www.sciss.de/temp/swap-spaces/swap-spaces-cover-${idx}%03d.jpg",
    "-H", s"api-key:$apiKey",
    "https://api.deepai.org/api/colorizer"
  )
  val resGen: String = cmdGen.!!
//   println(resGen)
  val i = resGen.indexOf("https://api.deepai")
  if (i < 0) println("Uh oh")
  val j = resGen.indexOf("\"", i + 1)
  val urlOut = resGen.substring(i, j)
  val fOut = userHome / "Downloads" / f"swap-spaces-cover-${idx}%03dc-low.jpg"
  val cmdGet = Seq("curl",
    urlOut,
    "-o", fOut.path,
    urlOut
  )
  val resGet /*: Int */ = cmdGet.!!
  // println(if (resGet == 0) "Ok" else "Error")
}

for (idx <- 221 to 230) {
  run(idx)
}
