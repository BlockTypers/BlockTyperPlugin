package com.blocktyper.v1_16_5.plugin;

public abstract class BlockTyperLoggerPlugin extends BlockTyperLocalePlugin {

	public BlockTyperLoggerPlugin() {
		super();
	}

	/////////////
	// LOGGING///
	/////////////

	public void info(String info) {
		info(info, null);
	}

	public void info(String warning, Integer mode) {
		log(warning, mode, false, null);
	}

	public void info(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, false, stackTraceCount);
	}

	public void warning(String warning) {
		log(warning, null, true, null);
	}

	public void warning(String warning, Integer mode) {
		log(warning, mode, true, null);
	}

	public void warning(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, true, stackTraceCount);
	}

	public void debugInfo(String info) {
		if (!config.debugEnabled())
			return;
		log(info, METHOD_NAME, false, null);
	}

	public void debugInfo(String info, Integer mode) {
		if (!config.debugEnabled())
			return;
		log(info, mode, false, null);
	}

	public void debugInfo(String info, Integer mode, Integer stackTraceCount) {
		if (!config.debugEnabled())
			return;
		log(info, mode, false, stackTraceCount);
	}

	public void debugWarning(String warning) {
		if (!config.debugEnabled())
			return;
		log(warning, METHOD_NAME, true, DEFAULT_WARNING_STACK_TRACE_COUNT);
	}

	public void debugWarning(String warning, Integer mode) {
		if (!config.debugEnabled())
			return;
		log(warning, mode, true, DEFAULT_WARNING_STACK_TRACE_COUNT);
	}

	public void debugWarning(String warning, Integer mode, Integer stackTraceCount) {
		if (!config.debugEnabled())
			return;
		log(" [DEBUG] " + warning, mode, true, stackTraceCount);
	}

	public void section(boolean isWarning, String line) {
		if (isWarning) {
			getLogger().info(line);
		} else {
			getLogger().warning(line);
		}
	}

	public void section(boolean isWarning) {
		section(isWarning, EMPTY);
	}

	///////////////////////
	// PROTECTED HELPERS///
	///////////////////////
	protected void log(String info, Integer mode, boolean isWarning, Integer stackTraceCount) {
		if (mode != null && (mode.equals(DASHES_TOP) || mode.equals(DASHES_TOP_AND_BOTTOM))) {
			section(isWarning, DASHES);
		}

		String methodName = "";
		if (mode != null && mode.equals(METHOD_NAME)) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
			methodName = stackTraceElement == null ? "[]"
					: "[" + getSimpleClassName(stackTraceElement.getClassName()) + "."
							+ stackTraceElement.getMethodName() + " (" + stackTraceElement.getLineNumber() + ")] ";
		}

		if (isWarning) {
			getLogger().warning(methodName + info);
		} else {
			getLogger().info(methodName + info);
		}

		if (stackTraceCount != null && stackTraceCount >= 0) {
			printStackTrace(stackTraceCount);
		}

		if (mode != null && (mode.equals(DASHES_BOTTOM) || mode.equals(DASHES_TOP_AND_BOTTOM))) {
			section(isWarning, DASHES);
		}
	}

	protected void printStackTrace(int levelsBack) {
		section(false, HASHES);
		for (int i = 0; i <= levelsBack; i++) {
			StackTraceElement stackTraceElement = null;
			try {
				stackTraceElement = Thread.currentThread().getStackTrace()[i];

				if (stackTraceElement == null)
					continue;

				String className = "[" + stackTraceElement.getClassName() + "]";
				String methodName = "[" + stackTraceElement.getMethodName() + "]";
				int lineNumber = stackTraceElement.getLineNumber();

				getLogger().info("  --className: " + className);
				getLogger().info("  --methodName: " + methodName);
				getLogger().info("  --lineNumber: " + lineNumber);

			} catch (Exception e) {
			}

		}
		section(false, HASHES);
	}

	protected String getSimpleClassName(String className) {
		if (className == null || !className.contains("."))
			return className;

		return className.substring(className.lastIndexOf(".") + 1);
	}
}
