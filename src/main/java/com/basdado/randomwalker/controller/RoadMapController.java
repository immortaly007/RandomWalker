package com.basdado.randomwalker.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.basdado.randomwalker.config.ConfigService;
import com.basdado.randomwalker.config.OpenStreetMapConfiguration;
import com.basdado.randomwalker.exception.OverpassException;
import com.basdado.randomwalker.model.LatLonCoordinate;
import com.basdado.randomwalker.model.ReadOnlyRoadMap;
import com.basdado.randomwalker.model.RoadMap;
import com.basdado.randomwalker.overpass.OverpassCommunicator;
import com.basdado.randomwalker.util.SparseGrid;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

@Singleton
@Startup
public class RoadMapController {

	private static final double MIN_LATITUDE = -90;
	private static final double MAX_LATITUDE = 90;
	private static final double MIN_LONGITUDE = -180;
	private static final double MAX_LONGITUDE = 180;
	
	@Inject private ConfigService configService;
	@Inject private OverpassCommunicator overpassCommunicator;
	
	private int horizontalTileCount;
	private int verticalTileCount;
	private double tileSize;
	
	private RoadMap roadMap;
	private SparseGrid<TileInfo> tileInfos;
	
	@PostConstruct
	private void init() {
		
		initTileInfo();
		roadMap = new RoadMap();
		
	}
	
	public void loadTilesAround(LatLonCoordinate pos) {
		
		loadTile(getTileInfo(pos));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() + tileSize, pos.getLongitude())));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() + tileSize, pos.getLongitude() + tileSize)));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude(), pos.getLongitude() + tileSize)));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() - tileSize, pos.getLongitude() + tileSize)));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() - tileSize, pos.getLongitude())));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() - tileSize, pos.getLongitude() - tileSize)));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude(), pos.getLongitude() - tileSize)));
		loadTile(getTileInfo(new LatLonCoordinate(pos.getLatitude() + tileSize, pos.getLongitude() - tileSize)));
		
	}
	
	public ReadOnlyRoadMap getRoadMap() {
		return roadMap;
	}
	
	private void loadTile(TileInfo tile) {
		
		if (tile.isLoaded()) {
			return; // Tile already loaded
		}
		
		OpenStreetMapConfiguration osmConfig = configService.getOpenStreetMapConfiguration();
		
//		String query = new OverpassQuery()
//				.format(OutputFormat.XML)
//				.timeout(120)
//				.filterQuery()
//					.way()
//					.tagMultiple("highway", osmConfig.getHighwayTagValues())
//				.end()
//				.boundingBox(
//						tile.getSouthWestCorner().getLatitude(), 
//						tile.southWestCorner.getLongitude(),
//						tile.getNorthEastCorner().getLatitude(), 
//						tile.getNorthEastCorner().getLongitude())
//				.build();
//
		String highwayTypes = String.join("|", osmConfig.getHighwayTagValues());
		String query = "(way[\"highway\"~\"" + highwayTypes + "\"]"
				+ "(" + tile.getSouthWestCorner().getLatitude() 
				+ "," + tile.getSouthWestCorner().getLongitude()
				+ "," + tile.getNorthEastCorner().getLatitude()
				+ "," + tile.getNorthEastCorner().getLongitude()
				+ ")"
				+ ";>;);"
				+ "out;";
		try {
			overpassCommunicator.executeQuery(query, new RoadMapReader(roadMap));
			tile.setLoaded(true);
			
		} catch (OverpassException e) {
			throw new EJBException(e);
		}
	}
	
	private void initTileInfo() {
		
		tileSize = configService.getOpenStreetMapConfiguration().getTileSize();
		horizontalTileCount = (int)Math.ceil(((MAX_LONGITUDE - MIN_LONGITUDE) / tileSize));
		verticalTileCount = (int)Math.ceil(((MAX_LATITUDE - MIN_LATITUDE) / tileSize));
		
		tileInfos = new SparseGrid<>(horizontalTileCount, verticalTileCount, (i, j) -> new TileInfo(false, 
				new LatLonCoordinate(MIN_LATITUDE +  j    * tileSize, MIN_LONGITUDE +  i    * tileSize),
				new LatLonCoordinate(MIN_LATITUDE + (j+1) * tileSize, MIN_LONGITUDE + (i+1) * tileSize)));
		
	}
	
	private TileInfo getTileInfo(LatLonCoordinate pos) {
		
		int horizontalIdx = (int)Math.floor((pos.getLongitude() - MIN_LONGITUDE) / tileSize);
		int verticalIdx = (int)Math.floor((pos.getLatitude() - MIN_LATITUDE) / tileSize);
		
		return tileInfos.get(horizontalIdx, verticalIdx);
	}
	
	private static class TileInfo {
		
		private boolean loaded;
		private final LatLonCoordinate southWestCorner;
		private final LatLonCoordinate northEastCorner;
		
		public TileInfo(boolean loaded, LatLonCoordinate southWestCorner, LatLonCoordinate northEastCorner) {
			this.loaded = loaded;
			this.southWestCorner = southWestCorner;
			this.northEastCorner = northEastCorner;
		}
		
		public LatLonCoordinate getSouthWestCorner() {
			return southWestCorner;
		}
		
		public LatLonCoordinate getNorthEastCorner() {
			return northEastCorner;
		}
		
		public boolean isLoaded() {
			return loaded;
		}
		
		public void setLoaded(boolean loaded) {
			this.loaded = loaded;
		}			
	}
	
	
	private class RoadMapReader implements OsmHandler {

		private final RoadMap target;
		
		public RoadMapReader(RoadMap target) {
			this.target = target;
		}
		
		@Override
		public void complete() throws IOException {
//			target.clean();
		}

		@Override
		public void handle(OsmBounds bounds) throws IOException {
			// We don't care about bounds for the road map
		}

		@Override
		public void handle(OsmNode node) throws IOException {
			target.addNode(node);
			
		}

		@Override
		public void handle(OsmWay way) throws IOException {

			target.addWay(way);
			
		}

		@Override
		public void handle(OsmRelation arg0) throws IOException {
			// Not important for the road map
		}
		
	}
}
