def makeCover(idx: Int): Unit = {
  val dirBase = userHome / "Documents" / "projects" / "simularr" / "swap_space" / "catalog"
  val fCover = dirBase / "cover" / f"swap-spaces-cover-${idx}%03dc.jpg"
  val latexSource = s"""\\documentclass{article}
    |\\usepackage[paperwidth=423mm,paperheight=223mm]{geometry}
    |\\usepackage[utf8]{inputenc}
    |\\usepackage[absolute]{textpos}
    |\\usepackage{graphicx}
    |\\begin{document}
    |\\pagestyle{empty}
    |\\begin{textblock*}{216mm}(198.2mm,3.56mm)%
    |\\includegraphics[width=216mm]{${fCover.path}}
    |\\end{textblock*}
    |\\end{document}
  """.stripMargin
  import sys.process._
  val dirTex  = dirBase / "cover_comp"
  val fTex    = dirTex / "cover_base.tex"
  val fPDF    = fTex.replaceExt("pdf")
  fTex.delete()
  val fo = new java.io.FileOutputStream(fTex)
  fo.write(latexSource.getBytes("UTF-8"))
  fo.flush()
  fo.close()
  val cmdGen = Process(Seq("pdflatex", "-interaction=nonstopmode", fTex.path), dirTex)
  fPDF.delete()
  val resGen: Int = cmdGen.!
  println(if (resGen == 0) "Ok" else "Error")
  val fStamp = dirBase / "cover_2_converted_wp_c1.pdf"
  val fOut = dirBase / "cover_pdf" / f"swap-spaces-cover-${idx}%03d.pdf"
  val cmdStamp = Seq("pdftk", fPDF.path, "stamp", fStamp.path, "output", fOut.path)
  val resStamp: Int = cmdStamp.!
  println(if (resStamp == 0) "Ok" else "Error")
}

// makeCover(5)

for (idx <- 1 to 250) { makeCover(idx) }
