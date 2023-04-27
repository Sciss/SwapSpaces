/*
 *  CreateCoverImage.scala
 *  (SwapSpaces)
 *
 *  Copyright (c) 2023 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Affero General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swapspaces

import com.jhlabs.composite.{ColorComposite, DifferenceComposite}
import com.jhlabs.image.NoiseFilter
import de.sciss.file.*

import java.awt.image.BufferedImage
import java.awt.{Graphics2D, RenderingHints}
import java.io.{File, IOException}
import javax.imageio.plugins.jpeg.JPEGImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.{IIOImage, ImageIO, ImageTypeSpecifier, ImageWriteParam}
import scala.annotation.tailrec
import scala.math.abs
import scala.util.control.NonFatal

object CreateCoverImage {
  def main(args: Array[String]): Unit = {
    implicit val config: Config = Config()
    for imageIdx <- 1 to 250 do {
      run(imageIdx)
    }
  }

  def run(imageIdx: Int)(implicit config: Config): Unit = {
    val baseDir     = userHome / "Documents" / "projects" / "simularr" / "swap_space" / "catalog"
    val dirInGray   = baseDir / "cover_gray"
    val dirInColor  = baseDir / "cover_colr"
    val dirOut      = baseDir / "cover"
    val fileInGray  = dirInGray  / f"swap-spaces-cover-$imageIdx%03d.jpg"
    val fileInColor = dirInColor / f"swap-spaces-cover-$imageIdx%03dc-low.jpg"
    val fileOut     = dirOut     / f"swap-spaces-cover-$imageIdx%03dc.jpg"
    if fileOut.exists() then {
      println(s"File ${fileOut.name} already exists. Not overwriting!")
      return
    }

    import config.*

    val imgInGray   = readScaleImage(fileInGray )
    val imgInColor  = readScaleImage(fileInColor)

    val wD        = scaleExtent
    val hD        = scaleExtent
    val imgOut    = new BufferedImage(wD, hD, BufferedImage.TYPE_INT_ARGB)
    val gD        = imgOut.createGraphics()
    val cmpNorm   = gD.getComposite
    val cmpColor  = new ColorComposite(1f)
    val fltNoise  = new NoiseFilter
    fltNoise.setAmount(noiseAmount)
    fltNoise.setDistribution(noiseDistribution)
//    val arrD      = new Array[Int](scaleExtent * scaleExtent)

    gD.setComposite(cmpNorm)
    if noiseAmount == 0 then gD.drawImage(imgInGray, 0, 0, null) else gD.drawImage(imgInGray, fltNoise, 0, 0)
    gD.setComposite(cmpColor)
    gD.drawImage(ensureARGB(imgInColor), 0, 0, null)

//    def calcDiff(): Double = {
//      imgOut.getRGB(0, 0, wD, hD, arrD, 0, wD)
//      var i = 0
//      var sum = 0.0
//      while (i < arrD.length) {
//        val argb = arrD(i)
//        val gray = (((argb >> 16) & 0xFF) + ((argb >> 8) & 0xFF) + (argb & 0xFF)) / 765.0
//        sum += gray
//        i += 1
//      }
//      sum
//    }

    require (!fileOut.exists())
    writeImage(imgOut, fileOut)
  }

  case class Config(
                     verbose          : Boolean = false,
                     scaleExtent      : Int     = 2048,
                     quality          : Int     =   98,
                     noiseAmount      : Int     =   20,
                     noiseDistribution: Int     = 1   // 0 - gaussian, 1 - uniform
                   ) {

    require (scaleExtent >= 2)
    require (noiseAmount >= 0 && noiseAmount <= 255)
    require (noiseDistribution >= 0 && noiseDistribution <= 1)
  }

  def readScaleImage(fileIn: File)(implicit config: Config): BufferedImage = {
    import config.*
    val imgIn0  = try {
      ImageIO.read(fileIn)
    } catch {
      case NonFatal(ex) =>
        throw new IOException(s"For ${fileIn.path}", ex)
    }
    if verbose then {
      println(s"Raw input size ${imgIn0.getWidth}, ${imgIn0.getHeight}")
    }
    val imgInC = if imgIn0.getWidth == scaleExtent && imgIn0.getHeight == scaleExtent then imgIn0 else {
      val res   = new BufferedImage(scaleExtent, scaleExtent, BufferedImage.TYPE_INT_RGB)
      val g     = res.createGraphics()
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION , RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      g.setRenderingHint(RenderingHints.KEY_RENDERING     , RenderingHints.VALUE_RENDER_QUALITY)
      g.drawImage(imgIn0, 0, 0, scaleExtent, scaleExtent, null)
      g.dispose()
      res
    }

    if verbose then {
      println(s"Scaled input size ${imgInC.getWidth}, ${imgInC.getHeight}")
    }

    imgInC
  }

  def ensureType(in: BufferedImage, tpe: Int): BufferedImage =
    if in.getType == tpe then in else copyImage(in, tpe)

  def copyImage(in: BufferedImage, tpe: Int): BufferedImage = {
    val b = new BufferedImage(in.getWidth, in.getHeight, tpe)
    val g = b.createGraphics()
    g.drawImage(in, 0, 0, null)
    g.dispose()
    b
  }

  def ensureARGB(in: BufferedImage): BufferedImage = ensureType(in, BufferedImage.TYPE_INT_ARGB)
  def ensureRGB (in: BufferedImage): BufferedImage = ensureType(in, BufferedImage.TYPE_INT_RGB)

  def writeImage(imgOut: BufferedImage, fileOut: File)(implicit config: Config): Unit = {
    val (fmtOut, imgParam) = fileOut.extL match {
      case ext @ "png" => (ext, null)
      case _ =>
        val p = new JPEGImageWriteParam(null)
        p.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        p.setCompressionQuality(config.quality * 0.01f)
        ("jpg", p)
    }

    val imgOutT = ensureRGB(imgOut)
    val it = ImageIO.getImageWriters(ImageTypeSpecifier.createFromRenderedImage(imgOutT), fmtOut)
    if !it.hasNext then throw new IllegalArgumentException(s"No image writer for $fmtOut")
    val imgWriter = it.next()
    fileOut.delete()
    val fos = new FileImageOutputStream(fileOut)
    try {
      imgWriter.setOutput(fos)
      imgWriter.write(null /* meta */ ,
        new IIOImage(imgOutT, null /* thumb */ , null /* meta */), imgParam)
      imgWriter.dispose()
    } finally {
      fos.close()
    }
  }
}
