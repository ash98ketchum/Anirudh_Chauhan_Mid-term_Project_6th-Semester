import { useRef } from "react";
import { motion, useScroll, useTransform } from "framer-motion";

export const HeroSection = () => {
  const containerRef = useRef(null);
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start start", "end start"],
  });

  const yText = useTransform(scrollYProgress, [0, 1], [0, 300]);
  const opacityText = useTransform(scrollYProgress, [0, 0.5], [1, 0]);
  const scaleBg = useTransform(scrollYProgress, [0, 1], [1, 1.1]);

  // Animation variants for staggered reveal
  const containerVars = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.3,
      },
    },
  };

  const itemVars = {
    hidden: { opacity: 0, y: 40 },
    show: { 
      opacity: 1, 
      y: 0, 
      transition: { 
        type: "spring", 
        stiffness: 100, 
        damping: 20 
      } 
    },
  };

  return (
    <section 
      ref={containerRef} 
      className="relative h-screen min-h-[800px] flex items-center justify-center overflow-hidden bg-black"
    >
      {/* Background with Subtle Parallax & Scale */}
      <motion.div 
        className="absolute inset-0 z-0 opacity-40"
        style={{ scale: scaleBg }}
      >
        <div className="absolute inset-0 bg-gradient-to-b from-black/20 via-black/50 to-black z-10" />
        <img 
          src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=2564&auto=format&fit=crop" 
          alt="Abstract Architecture" 
          className="w-full h-full object-cover"
        />
      </motion.div>

      {/* Hero Content */}
      <motion.div 
        className="relative z-10 text-center px-6 max-w-5xl mx-auto mt-20"
        style={{ y: yText, opacity: opacityText }}
        variants={containerVars}
        initial="hidden"
        animate="show"
      >
        <motion.div variants={itemVars} className="mb-4 inline-block">
          <span className="px-4 py-1.5 rounded-full border border-white/20 bg-white/5 backdrop-blur-md text-sm font-medium tracking-wide uppercase text-white/80">
            Now Hiring
          </span>
        </motion.div>
        
        <motion.h1 
          variants={itemVars}
          className="text-6xl md:text-8xl lg:text-9xl font-bold tracking-tighter text-white mb-6 text-balance leading-[1.05]"
        >
          Build the <br/>
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-white to-white/40">
            Impossible.
          </span>
        </motion.h1>
        
        <motion.p 
          variants={itemVars}
          className="text-xl md:text-2xl text-white/70 max-w-2xl mx-auto text-balance leading-relaxed"
        >
          Join a team of extraordinary minds. We craft products that define eras and push boundaries.
        </motion.p>
      </motion.div>

      {/* Scroll Indicator */}
      <motion.div 
        className="absolute bottom-10 left-1/2 -translate-x-1/2 z-10 flex flex-col items-center opacity-50"
        initial={{ opacity: 0 }}
        animate={{ opacity: 0.5 }}
        transition={{ delay: 1.5, duration: 1 }}
      >
        <span className="text-[10px] uppercase tracking-[0.2em] mb-2 font-medium">Scroll</span>
        <motion.div 
          className="w-[1px] h-12 bg-gradient-to-b from-white to-transparent"
          animate={{
            scaleY: [0, 1, 0],
            originY: [0, 0, 1],
            opacity: [0, 1, 0]
          }}
          transition={{
            duration: 2,
            repeat: Infinity,
            ease: "easeInOut"
          }}
        />
      </motion.div>
    </section>
  );
};
