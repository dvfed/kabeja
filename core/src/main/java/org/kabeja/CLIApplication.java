/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.kabeja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.processing.xml.SAXProcessingManagerBuilder;

public class CLIApplication implements Application {
	
	private String processFile;
	private String sourceFile;
	private String destinationFile;

	private boolean optionsIncomplete = false;

	private ProcessingManager processorManager;
	private String pipeline;
	
	public void start(Map<String, String> properties) {
		// setup application
		if (properties.containsKey("pp")) {
			this.processFile = (String) properties.get("pp");
		} else {
			this.processFile = "conf/process.xml";
		}

		if (properties.containsKey("pipeline")) {
			this.setPipeline((String) properties.get("pipeline"));
		} else {
			this.optionsIncomplete = true;
		}

		if (properties.containsKey("in")) {
			this.setSourceFile((String) properties.get("in"));
		} else {
			this.optionsIncomplete = true;
		}

		if (properties.containsKey("o")) {
			this.setDestinationFile((String) properties.get("o"));
		}

		this.initialize();

		if (optionsIncomplete || properties.containsKey("help")) {
			printUsage();
			printPipelines();
			return;
		}

		process();
	}

	public void stop() {
		// not needed
	}

	public void initialize() {
		try (InputStream in = new FileInputStream(this.processFile)) {
			this.setProcessConfig(in);
		} catch (IOException e) {
			System.out.println("Can't find process config file: " + this.processFile);
			this.optionsIncomplete = true;
		}
	}

	public void process() {
		File source = new File(this.sourceFile);
		if (! source.exists()) {
			System.out.println("Can't find input file or directory: " + this.sourceFile);
			return;
		}

		String destSuffix = this.processorManager.getProcessPipeline(pipeline).getGenerator().getSuffix();

		if (source.isFile()) {
			if (this.destinationFile == null) {
				this.destinationFile = this.sourceFile.split("\\.")[0] + "." + destSuffix;
			}
			System.out.println("Converting " + source.getName() + " to " + this.destinationFile);
			parseFile(source, this.destinationFile);
		} else if (source.isDirectory()) {
			File destination = null;
			if (this.destinationFile != null) {
				destination = new File(this.destinationFile);
				destination = destination.isDirectory() ? destination : destination.getParentFile();
			} else {
				destination = source;
			}
			
			File[] files = source.listFiles();
			for (int i = 0; i < files.length; i++) {
				String file = files[i].getName();
				String[] parts = file.split("\\.");
				File result = new File(destination.getAbsolutePath(), parts[0] + "." + destSuffix);
				System.out.println("Converting " + file + " to " + result);
				parseFile(files[i], result.getAbsolutePath());
			}
		}
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getDestinationFile() {
		return destinationFile;
	}

	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
	}

	public void setProcessConfig(InputStream in) {
		this.processorManager = SAXProcessingManagerBuilder.buildFromStream(in);
	}

	public void setPipeline(String name) {
		this.pipeline = name;
	}

	private void parseFile(File source, String output) {
		try {
			String extension = source.getName().toLowerCase();
			int index = extension.lastIndexOf('.');
			if (index > -1 && index + 1 < extension.length()) {
				extension = extension.substring(index + 1);
			}

			this.processorManager.process(
					new FileInputStream(source),
					extension,
					new HashMap<String, Object>(),
					pipeline,
					new FileOutputStream(output)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printUsage() {
		System.out
				.println("\nUsage: java -jar launcher.jar -cli <Options>"
						+ "\n\nOptions:\n"
						+ "  --help shows this and exit\n"
						+ "  -pp process.xml set processing file to use\n"
						+ "      defaults to 'conf/process.xml'\n"
						+ "  -pipeline name  process the given pipeline\n"
						+ "  -in <Input file or directory>\n"
						+ "  -o  <Output file or directory>\n"
						+ "      defaults to <input> with extension depending on pipeline\n"
				);
	}

	private void printPipelines() {
		if (this.processorManager == null) {
			System.out.println("No pipelines available.");
			return;
		}

		System.out.println("Available pipelines:");
		System.out.println("----------------------");
		
		Iterator<String> i = this.processorManager.getProcessPipelines().keySet().iterator();
		while (i.hasNext()) {
			String pipeline = (String) i.next();
			ProcessPipeline pp = this.processorManager.getProcessPipeline(pipeline);
			System.out.print("  " + pipeline);
			if (pp.getDescription().length() > 0) {
				System.out.print("\t" + pp.getDescription());
			}
			System.out.println();
		}
	}

}
