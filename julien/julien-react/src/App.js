import React, { Component } from 'react';
import './App.css';

class App extends Component {

  constructor(){
    super();
    //vars
    [this.mouseCX, this.mouseCY] = [0,0]; //current mouse position
    [this.mouseOX, this.mouseOY] = [0,0]; //last mouse position
    this.mouseMovement = 0;
    this.maxMouseMovement = 0;
    //consts
    this.pollMouseInterval = 250;
    this.topMouseMovement = 100;
    //bindings
    this.trackMouse = this.trackMouse.bind(this);
  }

  render() {
    return (
      <div className="App"
        ref={(appDiv) => this.appDiv = appDiv}
        onMouseMove={(e)=>this.trackMouse(e)}>

      </div>
    );
  }

  componentDidMount(){
    setInterval(() => this.pollMouseMovement(this),this.pollMouseInterval);
    console.log(this.appDiv);
  }

  trackMouse(e){
    [this.mouseCX,this.mouseCY] = [e.clientX,e.clientY];
  }

  pollMouseMovement(that){
    that.mouseMovement = Math.sqrt(
        Math.pow(that.mouseCX - that.mouseOX,2) +
        Math.pow(that.mouseCY - that.mouseOY,2));
    [that.mouseOX,that.mouseOY] = [that.mouseCX,that.mouseCY]
    that.maxMouseMovement = Math.max(that.maxMouseMovement,that.mouseMovement);
    var mouseMovementRate = that.mouseMovement / that.topMouseMovement;
    var opacity = Math.min(0.5+mouseMovementRate,1);
    that.appDiv.style.opacity = opacity;
    console.log(`MVMT ${that.mouseMovement} / ${that.maxMouseMovement} / ${opacity}`);
  }
}

export default App;
