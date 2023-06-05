import React, { Component } from 'react';
import axios from 'axios';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS } from 'chart.js/auto'

export default class BarChart extends Component {
    state = { 
        details: [], 
        flightsPerDate: {},
        labels: [],
        datasets: [{}],
    }

    componentDidMount() {
        axios.get(process.env.REACT_APP_SERVER_URL + '/api/flight/get/')
        .then(res => {
            const details = res.data;
            const flightsPerDate = this.flightsByDate(details)
            const labels = Object.keys(flightsPerDate)
            const datasets = [{
                label: "Flights per day",
                data: Object.values(flightsPerDate),
                backgroundColor: "#01bf71",
            }]
            this.setState({
                details,
                flightsPerDate,
                labels,
                datasets
            });
        })
        .catch(err => { })
    }

    extractDate(timestamp) {
        const date = new Date(timestamp * 1000);
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
    
        // Formatting the date as YYYY-MM-DD
        const formattedDate = `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    
        return formattedDate;
      }

    flightsByDate(details) {
        const flightsPerDate = {};

        details.forEach(element => {
            const date = this.extractDate(element.firstTimestamp);
            if(flightsPerDate.hasOwnProperty(date)) {
                flightsPerDate[date]++;
            } else {
                flightsPerDate[date] = 1;
            }
            
        });

        return flightsPerDate;
    }


    render() {
        const { flightsPerDate } = this.state
        const chartData = {
            labels: this.state.labels,
            datasets: this.state.datasets,
        }
    return (
        <div className='App'>
            <Bar data={chartData}></Bar>
        </div>
        )
    }
}
