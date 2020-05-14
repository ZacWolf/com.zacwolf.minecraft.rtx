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
final	File					outfile		=	new File(outdir+File.separator+file.getName());
final	ImageInputStream		is			=	ImageIO.createImageInputStream(file);
		try {
final	Iterator<ImageReader>	readers		=	ImageIO.getImageReaders(is);
			if (!readers.hasNext()) {
				throw new IllegalArgumentException("No reader for: " + file);
			}
final	ImageReader				reader		=	readers.next();
	        try {	            reader.setInput(is);
	        	if (reader.getWidth(0)<=size || reader.getHeight(0)<=size ) {
	        		FileUtils.copyFile(file, outfile);
	        	} else {
final	BufferedImage			input		=	reader.read(0, reader.getDefaultReadParam());
final	BufferedImageOp			resampler	=	new ResampleOp(size, size, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
final	BufferedImage			output		=	resampler.filter(input, null);
					if (ImageIO.write(output, type, outfile)) {
					   System.out.println("Created file:"+outfile);
					} else {
						throw new IOException("File:"+file.getName()+" unable to write");
					}
	        	}
			} finally {
	            reader.dispose();
	        }
		} finally {
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
				if (size == 128 || size == 256 || size == 512) {
final	File				dir			=	new File(new File(".").getCanonicalFile()+File.separator+"mcpack"+File.separator+size);
						if (dir.exists()) {
							for (int retry=0; retry<5; retry++) {
								try {
									FileUtils.deleteDirectory(dir);
									break;
								} catch (final IOException io) {
									if (retry>=5) {
										throw io;
									}
								}
							}
						}
final	File				outdir		=	new File(dir+File.separator+"textures"+File.separator+"blocks");
							outdir.mkdirs();
					try (Stream<Path> paths = Files.walk(Paths.get("./mcpack/src/textures/blocks"))) {
					    paths.filter(Files::isRegularFile).forEach(path -> {
					    	try {
final	String				file		=	path.toAbsolutePath().toFile().getCanonicalPath();
final	String				filename	=	path.getFileName().toString();
final	String				ext			=	filename.substring(filename.lastIndexOf(".")+1).toUpperCase();
									new ZacWolfRTX(new File(file),ext,size,outdir);
							} catch (final Exception e) {
								e.printStackTrace();
							}
				        });
					}
final	File				outdir2		=	new File(dir+File.separator+"textures"+File.separator+"entity"+File.separator+"bed");
							outdir2.mkdirs();
					try (Stream<Path> paths2 = Files.walk(Paths.get("./mcpack/src/textures/entity/bed"))) {
					    paths2.filter(Files::isRegularFile).forEach(path -> {
					    	try {
final	String				file		=	path.toAbsolutePath().toFile().getCanonicalPath();
final	String				filename	=	path.getFileName().toString();
final	String				ext			=	filename.substring(filename.lastIndexOf(".")+1).toUpperCase();
									new ZacWolfRTX(new File(file),ext,size,outdir2);
							} catch (final Exception e) {
								e.printStackTrace();
							}
				        });
					}
final	File				outdir3		=	new File(dir+File.separator+"textures"+File.separator+"entity"+File.separator+"chest");
							outdir3.mkdirs();
					try (Stream<Path> paths3 = Files.walk(Paths.get("./mcpack/src/textures/entity/chest"))) {
					    paths3.filter(Files::isRegularFile).forEach(path -> {
					    	try {
final	String				file		=	path.toAbsolutePath().toFile().getCanonicalPath();
final	String				filename	=	path.getFileName().toString();
final	String				ext			=	filename.substring(filename.lastIndexOf(".")+1).toUpperCase();
									new ZacWolfRTX(new File(file),ext,size,outdir3);
							} catch (final Exception e) {
								e.printStackTrace();
							}
				        });
					}
				}
			} else {
				new HelpFormatter().printHelp("ZacWolfRTX", options);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}