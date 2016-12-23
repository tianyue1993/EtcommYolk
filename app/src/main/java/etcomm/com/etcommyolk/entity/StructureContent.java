package etcomm.com.etcommyolk.entity;

import java.util.List;

public class StructureContent extends Content {

	public int code;
	public String message;
	public Content content;

	public class Content {
		public List<StructureItems> structure;

	}
}
