import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Map, GoogleApiWrapper, Marker } from 'google-maps-react';
import CustomMarker from '../routes/plane.png';

const MapContainer = ({ google }) => {
    const [details, setDetails] = useState([]);
    const [loading, setLoading] = useState(true);

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

    if (loading) {
        return <div>Loading...</div>;
    }

    const markerIcon = {
        url: CustomMarker,
        scaledSize: new google.maps.Size(50, 50),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(16, 32),
    };

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
                />
                )
            );
            })}
        </Map>
    );
};

export default GoogleApiWrapper({
    apiKey: 'API_KEY',
})(MapContainer);
