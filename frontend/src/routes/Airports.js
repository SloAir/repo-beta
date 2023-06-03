import React from 'react';
import axios from 'axios';
import Table from '../components/Table';

class Airlines extends React.Component {

    state = { 
      details: [],
      modalOpen: false,
      editRow: null,
    }

    componentDidMount() {
      axios.get('http://localhost:8000/api/airport/get/')
        .then(res => {
          const data = res.data;
          const cleanedData = data.map(entry => {
            if (entry.website) {
              const parts = entry.website.split('/'); // Split the URL into parts using forward slash as the separator
              const modifiedWebsite = parts.slice(0, 3).join('/'); // Join the first three parts back together with forward slashes
              return {
                ...entry,
                website: modifiedWebsite
              };
            } else {
              return entry; // Skip entries without a website
            }
          });
          this.setState({
            details: cleanedData
          });
        })
        .catch(err => {

        });
    }
    
    

    handleEdit = id => {
      this.setState({ editRow: id, modalOpen: true, });
    }

    handleCloseModal = () => {
      this.setState({ modalOpen: false, editRow: null });
    }  

    handleDelete = id => {
      const deletedIndex = this.state.details.findIndex(
        obj => obj._id === id
      );

      const deletedObject = this.state.details[deletedIndex];

      axios.delete(`http://localhost:8000/api/airport/delete/${deletedObject._id}/`)
      .then((response) => {
          console.log(response);

          const updatedData = [...this.state.details];
          updatedData.splice(deletedIndex, 1);
          this.setState({ details: updatedData });
      })
      .catch((error) => {
        console.error(error);
      });
    }

    handleSubmit = formData => {
      const editedIndex = this.state.details.findIndex(
        obj => obj._id == formData._id
      );
      
      axios.put('http://localhost:8000/api/airport/put/', formData)
      .then((response) => {
          console.log(response);

          const updatedData = [...this.state.details];
          updatedData[editedIndex] = formData;
          this.setState({ details: updatedData });
      })
      .catch((error) => {
        console.error(error);
      });
    }


    render() {
        const formDataStructure = {
          name: '',
          code: {
            iata: '',
            icao: '',
          },
          position: {
            country: {
              name: '',
            },
          },
          timezone: {
            abbr: '',
          },
          website: '',
        };

        return (
          <div className='App'>
            <Table 
              data={this.state.details} 
              formDataStruct={formDataStructure}
              onEdit={this.handleEdit}
              onSubmit={this.handleSubmit}
              onDelete={this.handleDelete}
            />
          </div>
        )
    }
}

export default Airlines;
