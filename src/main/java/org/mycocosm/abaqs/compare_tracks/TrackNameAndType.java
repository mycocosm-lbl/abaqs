package org.mycocosm.abaqs.compare_tracks;

public class TrackNameAndType {
	protected final String portalId;
	protected final String name;
	protected final FeatureTrackType type;
	public TrackNameAndType(String portalId, String name, FeatureTrackType type) {
		this.portalId = portalId;
		this.name = name;
		this.type = type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((portalId == null) ? 0 : portalId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackNameAndType other = (TrackNameAndType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (portalId == null) {
			if (other.portalId != null)
				return false;
		} else if (!portalId.equals(other.portalId))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String.format("%s:%s",portalId,name);
	}
}
