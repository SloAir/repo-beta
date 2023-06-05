import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Map, GoogleApiWrapper, Marker, InfoWindow } from 'google-maps-react';
import CustomMarker from '../routes/plane.png';

const MapContainer = ({ google }) => {
    const [details, setDetails] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeMarker, setActiveMarker] = useState(null);
    const [selectedPlace, setSelectedPlace] = useState(null);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            console.log("axios");
            const res = await axios.get('http://localhost:8000/api/get/');
            setDetails(res.data);
            setLoading(false);
        } catch (error) {
            console.error(error);
            setLoading(false);
        }
    };

    const markerIcon = {
        url: CustomMarker,
        scaledSize: new google.maps.Size(50, 50),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(16, 32),
    };

    const handleMarkerClick = (props, marker) => {
        setActiveMarker(marker);
        setSelectedPlace(props);
    };

    const handleCloseInfoWindow = () => {
        setActiveMarker(null);
        setSelectedPlace(null);
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <Map
            google={google}
            style={{ width: '100%', height: '100%' }}
            zoom={10}
            initialCenter={{ lat: 46.6, lng: 15.6 }}
        >
            {details.map((detail, index) => {
                const lastTrailIndex = detail.flight.trail.length - 1;
                const lastTrail = detail.flight.trail[lastTrailIndex];

                return (
                    lastTrail && (
                        <Marker
                            key={index}
                            position={{ lat: lastTrail.lat, lng: lastTrail.lng }}
                            options={{ icon: markerIcon }}
                            onClick={handleMarkerClick}
                            name={detail.flight.identification.id} // Assuming `detail` has a `name` property
                            description={
                                detail.flight.trail[0].lat + ", " + detail.flight.trail[0].lng
                            } // Assuming `detail` has a `description` property
                        />
                    )
                );
            })}

            <InfoWindow
                marker={activeMarker}
                visible={activeMarker !== null}
                onClose={handleCloseInfoWindow}
            >
                <div>
                    <p>{selectedPlace && selectedPlace.name}</p>
                    <p>{selectedPlace && selectedPlace.description}</p>
                </div>
            </InfoWindow>
        </Map>
    );
};

export default GoogleApiWrapper({
    apiKey: 'AIzaSyD5aGaOCX7ykXOHgQg9zkXwGArsbRrEqi4',
})(MapContainer);