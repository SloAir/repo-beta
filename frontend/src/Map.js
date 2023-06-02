import { Component } from 'react';
import { Map, GoogleApiWrapper, Marker } from 'google-maps-react';
import CustomMarker from '../src/plane.png';

class MapContainer extends Component {
    render() {
        const markerIcon = {
            url: CustomMarker,
            scaledSize: new this.props.google.maps.Size(50, 50),
            origin: new this.props.google.maps.Point(0, 0),
            anchor: new this.props.google.maps.Point(16, 32)
          };
        

        return(
            <Map google={this.props.google} style={{width:"100%", height:"100%"}} 
                    zoom={10} initialCenter={{ lat: 46.6, lng: 15.6 }}>
                <Marker position={{lat: 46.6, lng: 15.6 }} options={{
                    icon: markerIcon }}/>
            </Map>
        );
    }
}

export default GoogleApiWrapper({
    apiKey:"AIzaSyD5aGaOCX7ykXOHgQg9zkXwGArsbRrEqi4"
})(MapContainer)