package me.retrodaredevil.couchdbjava.request;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ViewQuery {
	private final String designDoc;
	private final String viewName;
	private final ViewQueryParams params;

	public ViewQuery(String designDoc, String viewName, ViewQueryParams params) {
		this.designDoc = requireNonNull(designDoc);
		this.viewName = requireNonNull(viewName);
		this.params = requireNonNull(params);
	}

	public String getDesignDoc() {
		return designDoc;
	}

	public String getViewName() {
		return viewName;
	}

	public ViewQueryParams getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "ViewQuery(" +
				"designDoc='" + designDoc + '\'' +
				", viewName='" + viewName + '\'' +
				", params=" + params +
				')';
	}
}
