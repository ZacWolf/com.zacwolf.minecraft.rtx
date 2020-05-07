/**
 *
 */
package com.zacwolf.minecraft.rtx;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import com.twelvemonkeys.image.ResampleOp;


/**
 * @author Zac Morris <zac@zacwolf.com>
 *
 */
public class ZacWolfRTX {

	/**
	 * @throws IOException
	 *
	 */
	public ZacWolfRTX(final File file,final String type, final int size,final File outdir) throws IllegalArgumentException,IOException{
final	ImageInputStream		is		=	ImageIO.createImageInputStream(file);
		try {
final	Iterator<ImageReader>	readers	=	ImageIO.getImageReaders(is);
			if (!readers.hasNext()) {
				throw new IllegalArgumentException("No reader for: " + file);
			}
final	ImageReader				reader	=	readers.next();
	        try {	            reader.setInput(is);
	        	if (reader.getHeight(0)>size) {
final	BufferedImage			input		=	reader.read(0, reader.getDefaultReadParam());
final	BufferedImageOp			resampler	=	new ResampleOp(size, size, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
final	BufferedImage			output		=	resampler.filter(input, null);
final	File					outfile		=	new File(outdir+File.separator+file.getName());
					if (ImageIO.write(output, type, outfile)) {
					   System.out.println("Created file:"+outfile);
					} else {
						throw new IOException("File:"+file.getName()+" unable to write");
					}
	        	}
			} finally {
	            // Dispose reader in finally block to avoid memory leaks
	            reader.dispose();
	        }
		} finally {
	        // Close stream in finally block to avoid resource leaks
	        is.close();
	    }
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
final	Options				options		= new Options();
							options.addOption("v", "version", true, "Version")
									.addOption("s", "size", true, "Texture size");

final	CommandLineParser	parser		=	new DefaultParser();
		try {
final	CommandLine			cmd			=	parser.parse( options, args);
			if (cmd.hasOption("s")) {
final	int					size		=	Integer.parseInt(cmd.getOptionValue("s"));
				if (size == 128 || size == 256) {
final	File				dir			=	new File(new File(".").getCanonicalFile()+File.separator+"mcpack"+File.separator+size);
						if (dir.exists()) {
							FileUtils.deleteDirectory(dir);
						}
final	File				outdir		=	new File(dir+File.separator+"textures"+File.separator+"blocks");
							outdir.mkdirs();
					try (Stream<Path> paths = Files.walk(Paths.get("./images/textures/blocks"))) {
					    paths
					        .filter(Files::isRegularFile)
					        .forEach(path -> {
					        	try {
final	String				file		=	path.toAbsolutePath().toFile().getCanonicalPath();
final	String				filename	=	path.getFileName().toString();
final	String				ext			=	filename.substring(filename.lastIndexOf(".")+1).toUpperCase();
									new ZacWolfRTX(new File(file),ext,size,outdir);
								} catch (final IOException e) {
									e.printStackTrace();
								}
					        });
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
final	HelpFormatter		formatter	= new HelpFormatter();
      						formatter.printHelp("ZacWolfRTX", options);
		}
	}
}