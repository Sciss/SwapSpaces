def runColorize(idx: Int): Unit = {
  println(s"Processing image $idx...")
  import sys.process._
  val dirBase = userHome / "Documents" / "projects" / "simularr" / "swap_space" / "catalog"
  val dirCode = userHome / "Documents" / "devel" / "colorization"
  val fIn  = dirBase / "cover_gray" / f"swap-spaces-cover-${idx}%03d.jpg"
  val fTmp = dirCode / "saved_siggraph17.jpg"
  val fOut = dirBase / "cover_colr" / f"swap-spaces-cover-${idx}%03dc-low.jpg"
  require (fIn.exists(), s"Input ${fIn.path} does not exist")
  require (!fOut.exists(), s"Output ${fOut.path} already exists")
  
  val cmdGen = Process(Seq("python3", "catalog.py", "-i", fIn.path), dirCode)
  val resCode: Int = cmdGen.!
  println(if (resCode == 0) "Ok" else "Error")
  if (resCode == 0) {
    Seq("mv", fTmp.path, fOut.path).!!
  }
}

// runColorize(228)

for (idx <- 228 to 250) {
  runColorize(idx)
}
