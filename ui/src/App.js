import React, {Component} from 'react';
import {
  BrowserRouter as Router,
  Route,
} from 'react-router-dom';

import {NewGame} from './new-game'
import {ExistingGame} from './existing-game'

import './App.css';

class App extends Component {
  render() {
    return (
      <Router>
          <Route path="/game" exact component={ExistingGame}/>
          <Route path="/" exact component={NewGame}/>
        
      </Router>
    );
  }
}

export default App;
