def joinCovers(): Unit = {
  val dirBase = userHome / "Documents" / "projects" / "simularr" / "swap_space" / "catalog"
  val fSingleSq = (1 to 250).map { idx => dirBase / "cover_pdf" / f"swap-spaces-cover-${idx}%03d.pdf" }
  val fOut = dirBase / "swap-spaces-covers.pdf"
  val cmdJoin = Seq("pdftk") ++ fSingleSq.map(_.path) ++ Seq("cat", "output", fOut.path)
  import sys.process._
  val res: Int = cmdJoin.!
  println(if (res == 0) "Ok" else "Error")
}

joinCovers()
